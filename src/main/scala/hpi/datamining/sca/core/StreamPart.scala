package hpi.datamining.sca.core

import rx.lang.scala.Observable

private[core] trait StreamPart[T] {

  protected def out: Observable[T]

  def via[S](transformation: Transformation[T, S]): Transformation[T, S] = transformation.subscribe(out)

  def to(sink: Sink[T]): Unit = sink.subscribe(out)

  def transform[S](f: T => S): Transformation[T, S] = {
    this.via(new FuncTransformation[T, S](f))
  }
}
