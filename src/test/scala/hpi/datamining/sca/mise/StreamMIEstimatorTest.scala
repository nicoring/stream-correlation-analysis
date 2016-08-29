package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, EstimatorStream, EstimatorStreamFactory, WindowSpec}
import hpi.datamining.sca.mise.wrapper.MISESyncController
import org.scalatest.FunSpec
import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.ReplaySubject

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


class TestMiEstimator extends EstimatorStream {

  val data = new ArrayBuffer[(Double, Double)]

  override def addData(x: Double, y: Double): Unit = data += ((x, y))

  override def length: Long = data.length

  override def miQueryImpl(w: WindowSpec): EstimationResult = EstimationResult(1.0 / length, length)

  override val referenceImplementation: Boolean = false
  override val minPossibleQuerySize: Int = 0
  override val factory: EstimatorStreamFactory = new TestMiEstimatorFactory
}

class StreamMIEstimatorTest extends FunSpec {

  def getTestController(): MISEController = {
    val testMise = new TestMiEstimator
    new MISESyncController(testMise)
  }

  def addAllData(data: Seq[(Double, Double)], cont: MISEController): Unit = {
    for ((x, y) <- data) cont.addData(x, y)
  }

  describe("StreamMIEstimator") {

    it("should consume data from the passed observable") {
      val data: List[(Double, Double)] = List(1,2,3,4,5).map(x => (x.toDouble, x.toDouble))
      val estimator = getTestController()
      addAllData(data, estimator)
      assert(estimator.length == data.length)
    }

    it("should answer single mi queries") {
      val data: List[(Double, Double)] = List(1,2,3,4,5).map(x => (x.toDouble, x.toDouble))
      val estimator = getTestController()
      addAllData(data, estimator)
      val w = WindowSpec(0,0)
      val EstimationResult(res,_) = Await.result(estimator.query(w), Duration.Inf)
      assert(res == 1.0 / data.length)
    }

    it("should answer continuous query") {
      val data: List[(Double, Double)] = List(1,2,3,4,5).map(x => (x.toDouble, x.toDouble))
      val estimator = getTestController()

      val w = WindowSpec(0,0)
      val oresults = estimator.addContinuousQuery(w)

      oresults.doOnError(_.printStackTrace())

      val fresults = oresults.toList.toBlocking.toFuture.map(_.map(_.mi))

      addAllData(data, estimator)
      estimator.asInstanceOf[MISESyncController].stop()

      val expected = List.tabulate(data.length)(n => 1.0 / (n + 1))
      val results = Await.result(fresults, Duration.Inf)

      assert(results == expected)
    }
  }

}
