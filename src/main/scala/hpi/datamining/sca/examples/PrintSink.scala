package hpi.datamining.sca.examples

import hpi.datamining.sca.core.Sink
import rx.lang.scala.{Observer, Subscriber}

class PrintSink[T] extends Sink[T] {
  override protected val subscriber: Observer[T] = new Subscriber[T]() {
    override def onStart() = println("Start")
    override def onNext(s: T) = println(s)
    override def onCompleted() = println("Finished")
  }
}
