package hpi.datamining.sca.mise.wrapper.sources

import scala.util.Random

class RandomSource(size: Int) extends MISESource {

  lazy val random = new Random

  val dataPoints = for (_ <- 1 to size) yield ramdomData()

  def ramdomData(): (Double, Double) = (random.nextDouble(), random.nextDouble())

  override def foreach(f: ((Double, Double)) => Unit): Unit = dataPoints.foreach(f)

  override def hasDefiniteSize: Boolean = true

  override def filter(f: ((Double, Double)) => Boolean): MISESource = new CollectionSource(dataPoints.filter(f))
}
