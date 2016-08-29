package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, EstimatorStream, EstimatorStreamFactory, WindowSpec}
import hpi.datamining.sca.actors.MISEActorController
import hpi.datamining.sca.mise.wrapper.MISESyncController

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Random

class TestMiEstimatorFactory extends EstimatorStreamFactory {
  override def build(): EstimatorStream = new TestMiEstimator

  override val estimatorDescription: String = "TestMiEstimator"
}

class TestMiEstimator extends EstimatorStream {

  val data = new ArrayBuffer[(Double, Double)]

  override def addData(x: Double, y: Double): Unit = data += ((x, y))

  override def length: Long = data.length

  override def miQueryImpl(windowSpec: WindowSpec): EstimationResult = EstimationResult(1.0 / length, length)

  override val referenceImplementation: Boolean = false
  override val minPossibleQuerySize: Int = 1
  override val factory: EstimatorStreamFactory = new TestMiEstimatorFactory
}

object MISETest {

  lazy val random = new Random

  def ramdomData(): (Double, Double) = (random.nextDouble(), random.nextDouble())

  def sinsin(i: Long): (Double, Double) = (math.sin(i), math.sin(i))

  def sincos(i: Long): (Double, Double) = (math.sin(i), math.cos(i))


  def main(args: Array[String]) {

    val mise = new ReferenceMISE(2, 10)
    val testMise = new TestMiEstimator
    val miseController = new MISESyncController(testMise)

    val w = WindowSpec(10, 100)
//      val results = miseController.continuousQuery(w)

//      results foreach { result =>
//        println("Query result: " + result)
//      }

    for (i <- 1 to 1000) {
//      val (x, y) = ramdomData()
      val (x, y) = sinsin(i)
//        val (x, y) = sincos(i)

      miseController.addData(x, y)
    }

    val results = miseController.addContinuousQuery(w)

    results.doOnError(_.printStackTrace())

    results foreach { result =>
      println("Cont. Query result: " + result)
    }

    for (i <- 1 to 10000) {
            val (x, y) = ramdomData()
//      val (x, y) = sinsin(i)
      //        val (x, y) = sincos(i)

      miseController.addData(x, y)
    }

    val qr = miseController.query(w)

    qr.foreach(println)

    qr.onFailure {
      case e: Exception => e.printStackTrace()
    }

    println(Await.result(qr, Duration.Inf))
  }
}
