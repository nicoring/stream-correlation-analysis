package hpi.datamining.sca.core

import rx.lang.scala.Observable

trait Source[T] extends StreamPart[T] {
  protected val observable: Observable[T]
  protected override def out = observable
}

class ObservableSource[T](val observable: Observable[T]) extends Source[T]

class ListSource[T](list: List[T]) extends ObservableSource(Observable.from(list))