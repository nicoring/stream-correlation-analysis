package hpi.datamining.sca.actors

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import dmf.stream.mutinf.{EstimationResult, EstimatorStream, WindowSpec}
import hpi.datamining.sca.actors.MISEActor.{Data, Query, QueryResult}
import hpi.datamining.sca.actors.MISERouterActor.{Add, Remove}
import hpi.datamining.sca.actors.TriggerActor.TriggerSpec
import hpi.datamining.sca.mise.MISEController
import rx.lang.scala.subjects.ReplaySubject
import rx.lang.scala.Observable
import akka.util.Timeout

import scala.concurrent.duration._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object MISEActorController {
  case object NewData
}

class MISEActorController(mise: EstimatorStream) extends MISEController {
  import MISEActorController._

  implicit val system = ActorSystem("MISE-ActorSystem")
  implicit val timeout = Timeout(10 second)

  val miseActor = system.actorOf(MISEActor.props(mise), "miseFrameworkActor")

  val continousQueryActors: mutable.Map[Long, ActorRef] = mutable.Map()
  val triggerActors: mutable.Map[Long, ActorRef] = mutable.Map()

  val routerActor = system.actorOf(Props[MISERouterActor], "miseRouter" )

  def addToRouter(ref: ActorRef): Unit = {
    routerActor ! Add(ref)
  }

  def removeFromRouter(ref: ActorRef): Unit = {
    routerActor ! Remove(ref)
  }

  def handleNewData(): Unit = {
    routerActor ! NewData
  }

  override def addData(x: Double, y: Double): Unit = {
    miseActor ! Data(x, y)
    handleNewData()
  }

  override def query(windowSpec: WindowSpec): Future[EstimationResult] = (miseActor ? Query(windowSpec))
    .mapTo[QueryResult]
    .map(_.result)

  override def addContinuousQuery(windowSpec: WindowSpec): Observable[EstimationResult] = {
    val subject = ReplaySubject[EstimationResult]()

    val queryActor = system.actorOf(ContinuousQueryActor.props(miseActor, windowSpec, subject))

    addToRouter(queryActor)
    subject.doOnUnsubscribe({
      removeFromRouter(queryActor)
      queryActor ! PoisonPill
      subject.onCompleted()
    })

    subject
  }

  // todo: make cancelable
  override def addTriggerQuery(windowSpec: WindowSpec,
                   predicate: EstimationResult => Boolean,
                   onTrigger: EstimationResult => Unit): Unit = {
    val spec = TriggerSpec(windowSpec, predicate, onTrigger)
    val queryActor = system.actorOf(TriggerActor.props(miseActor, spec))
  }

  override def length: Long = mise.length
}
