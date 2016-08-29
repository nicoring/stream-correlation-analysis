package hpi.datamining.sca.mise

import dmf.stream.mutinf.{EstimationResult, WindowSpec}
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait StreamMIEstimator {
  def query(windowSpec: WindowSpec): EstimationResult
  def queryStream(windowSpec: WindowSpec): Observable[EstimationResult]
  def queryTriggerOnce[T](windowSpec: WindowSpec, pred: EstimationResult => Option[T]): Future[T]
  def queryTriggerStream[T](windowSpec: WindowSpec, pred: EstimationResult => Option[T]): Observable[T]
}

class StreamMIEstimatorImpl(val mise: MIEstimator, data: Observable[(Double, Double)]) extends StreamMIEstimator {

  data.foreach { case (x, y) => mise.addData(x, y) }

  override def query(windowSpec: WindowSpec): EstimationResult = mise.query(windowSpec)

  override def queryStream(windowSpec: WindowSpec): Observable[EstimationResult] = {
    data.map(_ => query(windowSpec))
  }

  override def queryTriggerStream[T](windowSpec: WindowSpec, pred: (EstimationResult) => Option[T]): Observable[T] = {
    queryStream(windowSpec).flatMap(res => Observable.from(pred(res)))
  }

  override def queryTriggerOnce[T](windowSpec: WindowSpec, pred: (EstimationResult) => Option[T]): Future[T] = {
    queryTriggerStream(windowSpec, pred).first.toBlocking.toFuture
  }
}
