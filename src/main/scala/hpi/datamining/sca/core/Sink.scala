package hpi.datamining.sca.core

import rx.lang.scala.{Observable, Observer}

trait Sink[-T] {
  protected val subscriber: Observer[T]
  private[core] def subscribe(observable: Observable[T]): Unit = observable.subscribe(subscriber)
}
