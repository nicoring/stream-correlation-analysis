package hpi.datamining.sca.actors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}
import akka.routing.{BroadcastRoutingLogic, Router}
import hpi.datamining.sca.actors.MISEActorController.NewData

object MISERouterActor {
  case class Add(actorRef: ActorRef)
  case class Remove(actorRef: ActorRef)
}

class MISERouterActor extends Actor {
  import MISERouterActor._

  var router = Router(BroadcastRoutingLogic())

  def hasRoutees: Boolean = router.routees.nonEmpty

  override def receive: Receive = inactive

  def inactive: Receive = {
    case Add(actorRef) =>
      context watch actorRef
      router = router.addRoutee(actorRef)
      context.become(active)
  }

  def active: Receive = {
    case Add(actorRef) =>
      context watch actorRef
      router = router.addRoutee(actorRef)
    case Remove(actorRef) =>
      router = router.removeRoutee(actorRef)
      if (!hasRoutees)
        context.become(inactive)
    case msg => router.route(msg, self)
  }
}
