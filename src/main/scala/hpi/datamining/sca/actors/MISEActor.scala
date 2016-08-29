package hpi.datamining.sca.actors

import akka.actor.{Actor, Props}
import dmf.stream.mutinf.{EstimationResult, EstimatorStream, WindowSpec}


object MISEActor {
  case class Data(x: Double, y: Double)
  case class Query(windowSpec: WindowSpec)
  case class QueryResult(result: EstimationResult)

  def props(mise: EstimatorStream): Props = Props(new MISEActor(mise))
}

class MISEActor(mise: EstimatorStream) extends Actor {
  import MISEActor._
  override def receive: Receive = {
    case Data(x, y) => mise.addData(x, y)
    case Query(windowSpec) =>
      val result = mise.miQuery(windowSpec)
      sender() ! QueryResult(result)
  }
}
