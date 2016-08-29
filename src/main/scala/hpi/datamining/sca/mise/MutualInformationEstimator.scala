package hpi.datamining.sca.mise

import dmf.stream.mutinf.{MarginalCountsQueryResult, QueryAnchor, QueryAnchorReference, WindowSpec}
import hpi.datamining.sca.mise.MutualInformationEstimator.DataPoint
import rx.lang.scala.Observable

import scala.util.Random

object MutualInformationEstimator {
  type DataPoint = (Double, Double)

  def main(args: Array[String]) {
    val elems = List(
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (0.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (1.0,0.0),
      (0.0,0.0),
      (0.0,0.0)
    )
    val input = Observable.just()
    val mise = new MutualInformationEstimator(2, 100, input)

    val mut = mise.query(4, 10)
    println("Mutual Information: " + mut)
  }
}

class MutualInformationEstimator(k: Int, alpha: Long, input: Observable[DataPoint]) {

  var count = 0
  var anchors = List[QueryAnchor]()
  val random = new Random()

  input.subscribe(dp => addDataPoint(dp))

  def nextCount(): Long = {
    val currentCount = count
    count += 1
    currentCount
  }

  def samplingProbability(n: Long): Double = if (n < alpha) 1.0 else alpha / n.toDouble

  def stepwiseSamplingProbability(n: Long): Double =
    if (n < alpha)
      1
    else
      samplingProbability(n) / samplingProbability(n - 1)

  def sampleAnchors(): Unit = {
    anchors = anchors.filter { anchor =>
      val rand = random.nextDouble()
      rand < stepwiseSamplingProbability(anchor.pos)
    }
  }

  def digamma(x: Double): Double = DigammaFunc.compute(x)

  def Ψ(x: Double): Double = digamma(x)

  def addDataPoint(dataPoint: DataPoint): Unit = {
    val pos = nextCount()
    val newAnchor = new QueryAnchorReference(dataPoint._1, dataPoint._2, pos, k)
    for (anchor <- anchors) {
      anchor.addData(dataPoint._1, dataPoint._2, pos)
      newAnchor.addData(anchor.x, anchor.y, anchor.pos)
    }
    anchors ::= newAnchor
    sampleAnchors()
  }

  def estimate(queryResult: MarginalCountsQueryResult) = {
    val MarginalCountsQueryResult(mcx, mcy, kNeighborDist, seenL, seenR) = queryResult
    val n = seenL + seenR
    Ψ(k) - Ψ(mcx + 1) - Ψ(mcy) + Ψ(n)
  }

  def mean(xs: Seq[Double]): Double = xs.sum / xs.size.toDouble

  def isInWindow(t1: Long, t2: Long, anchor: QueryAnchor): Boolean = t1 <= anchor.pos && t2 <= anchor.pos

  def query(t1: Long, t2: Long): Double = {
    val estimates = for {
      anchor <- anchors
      if isInWindow(t1, t2, anchor)
      queryResult <- anchor.getMarginalCounts(WindowSpec.createFromIndices(t1, t2))
    } yield estimate(queryResult)
    println("ESTIMATES length: " + estimates.size)
    mean(estimates)
  }
}
