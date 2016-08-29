package hpi.datamining.sca.sync

import dmf.stream.mutinf.{EstimationResult, EstimatorStream, WindowSpec}
import hpi.datamining.sca.mise.{MISEController, ReferenceMISE}
import hpi.datamining.sca.sync.queries.{ContinuousQuery, EmbeddedQuery, TriggerQuery}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.ReplaySubject

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MISESyncController {
  def apply(k: Int, reservoirSize: Int): MISESyncController = {
    val mise = new ReferenceMISE(k, reservoirSize)
    new MISESyncController(mise)
  }
}

class MISESyncController(mise: EstimatorStream) extends MISEController {

  val embeddedQueries = mutable.Map[String, EmbeddedQuery]()

  def stop() = {
    embeddedQueries.values
      .collect { case c: ContinuousQuery => c.subject}
      .foreach(_.onCompleted())
    embeddedQueries.clear()
  }

  def handleNewData() = {
    for (query <- embeddedQueries.values) query.handleNewData()
  }

  override def addData(x: Double, y: Double): Unit = {
    mise.addData(x, y)
    handleNewData()
  }

  def addQuery(embeddedQuery: EmbeddedQuery): String = {
    embeddedQueries += (embeddedQuery.id -> embeddedQuery)
    embeddedQuery.id
  }

  def removeQuery(id: String): Option[EmbeddedQuery] = {
    embeddedQueries.remove(id)
  }

  override def query(windowSpec: WindowSpec): Future[EstimationResult] = Future(mise.miQuery(windowSpec))

  def addContinuousQuery(spec: ContinuousQuerySpec): Observable[EstimationResult] = {
    val subject = ReplaySubject[EstimationResult]()
    val query = new ContinuousQuery(mise, subject, spec)
    addQuery(query)

    subject
  }

  override def addContinuousQuery(windowSpec: WindowSpec): Observable[EstimationResult] = {
    val spec = ContinuousQuerySpec(windowSpec)
    addContinuousQuery(spec)
  }

  def addTriggerQuery(spec: TriggerQuerySpec): Unit = {
    val query = new TriggerQuery(mise, spec)
    addQuery(query)
  }

  override def addTriggerQuery(windowSpec: WindowSpec, predicate: (EstimationResult) => Boolean, onTrigger: (EstimationResult) => Unit): Unit = {
    val spec = TriggerQuerySpec(windowSpec, predicate, onTrigger)
    addTriggerQuery(spec)
  }

  override def length: Long = mise.length
}
