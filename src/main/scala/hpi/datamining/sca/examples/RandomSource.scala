package hpi.datamining.sca.examples

import hpi.datamining.sca.core.{DataFrame, Source}
import rx.lang.scala.Observable

import scala.util.Random

class RandomSource(count: Int) extends Source[Double] {

  val rands = Array.fill(count)(Random.nextDouble)

  override protected val observable: Observable[Double] = Observable.from(rands)
}
