package hpi.datamining.sca.actors

import akka.actor.{Actor, ActorRef, Props}
import dmf.stream.mutinf.{EstimationResult, WindowSpec}
import hpi.datamining.sca.actors.MISEActor.{Query, QueryResult}
import hpi.datamining.sca.actors.MISEActorController.NewData
import rx.lang.scala.Subject

object ContinuousQueryActor {
  def props(miseActor: ActorRef, windowSpec: WindowSpec, subject: Subject[EstimationResult]): Props =
    Props(new ContinuousQueryActor(miseActor, windowSpec, subject))
}

class ContinuousQueryActor(miseActor: ActorRef,
                           windowSpec: WindowSpec,
                           subject: Subject[EstimationResult]) extends Actor {
  override def receive: Receive = {
    case NewData => miseActor ! Query(windowSpec)
    case QueryResult(result) => subject.onNext(result)
  }
}
