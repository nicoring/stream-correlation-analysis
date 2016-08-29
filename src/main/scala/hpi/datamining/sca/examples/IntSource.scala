package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Source
import rx.lang.scala.Observable

class IntSource extends Source[Int] {
  val elems = List(1,2,3,4,5,6,7,8,9,10)
  override protected val observable: Observable[Int] = Observable.from(elems)
}
