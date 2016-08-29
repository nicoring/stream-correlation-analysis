package hpi.datamining.sca.core

import rx.lang.scala.{Observable, Subject}
import rx.lang.scala.subjects.ReplaySubject

trait Transformation[T, U] extends StreamPart[U] {

  protected val subject: Subject[T] = ReplaySubject[T]()

  protected def transform(observable: Observable[T]): Observable[U]

  protected def transformed: Observable[U] = transform(subject)

  private[core] def subscribe(observable: Observable[T]): Transformation[T, U] = {
    observable.subscribe(subject)
    this
  }

  protected override def out = transformed
}

object Transformation {
  def merge[U, S, T](t1: Transformation[U, T], t2: Transformation[S, T]): Source[(T, T)] = {
    val zipped = t1.transformed.zip(t2.transformed)
    new ObservableSource(zipped)
  }
}

class FuncTransformation[T, U](f: T => U) extends Transformation[T, U] {
  override protected def transform(observable: Observable[T]): Observable[U] = observable.map(f)
}
