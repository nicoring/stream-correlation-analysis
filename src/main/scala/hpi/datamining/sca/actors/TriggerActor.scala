package hpi.datamining.sca.actors


import akka.actor.{Actor, ActorRef, Props}
import dmf.stream.mutinf.{EstimationResult, WindowSpec}
import hpi.datamining.sca.actors.MISEActor.{Query, QueryResult}
import hpi.datamining.sca.actors.MISEActorController.NewData
import hpi.datamining.sca.actors.TriggerActor.TriggerSpec

object TriggerActor {

  case class TriggerSpec(windowSpec: WindowSpec, predicate: EstimationResult => Boolean, onTrigger: EstimationResult => Unit)

  def props(miseRef: ActorRef, spec: TriggerSpec): Props =
    Props(new TriggerActor(miseRef, spec))
}

class TriggerActor(miseRef: ActorRef, spec: TriggerSpec) extends Actor {

  val TriggerSpec(windowSpec, predicate, onTrigger) = spec

  def handleResult(result: EstimationResult) = {
    if (predicate(result)) {
      onTrigger(result)
    }
  }

  override def receive: Receive = {
    case NewData => miseRef ! Query(windowSpec)
    case QueryResult(result) => handleResult(result)
  }
}
