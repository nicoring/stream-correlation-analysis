package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Source
import rx.lang.scala.Observable

class ListSource[T](list: List[T]) extends Source[T] {
  override protected val observable: Observable[T] = Observable.from(list)
}
