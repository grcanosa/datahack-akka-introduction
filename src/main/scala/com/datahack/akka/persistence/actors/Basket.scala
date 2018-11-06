package com.datahack.akka.persistence.actors

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.datahack.akka.persistence.actors.Basket._


object Basket {

  case class Cmd(data:String)
  case class Evt(data:String)
  case object Print
  case object Snap
  case object Boom
  case class State(state:String)

  //Clase a utilizar para mantener el estado del actor. Esto es normalmente lo que se hace
  //Se guarda esta clase entera

  case class BasketActorState(events:List[String] = Nil) {
    def updated(event:Evt):BasketActorState = copy(event.data :: events)
    def size: Int = events.length

    override def toString: String = events.reverse.toString
  }

}





class Basket extends PersistentActor with ActorLogging {

  var state: BasketActorState = BasketActorState()

  def updateState(evt:Evt):Unit =
    state = state.updated(evt)

  def numEvents = state.size

  override def persistenceId: String = "bastet-persistence-actor"

  override def receiveRecover: Receive = {
    case evt:Evt => updateState(evt)
    case SnapshotOffer(_,snapshot:BasketActorState) =>
      log.info(s"Offered state= $snapshot")
      state = snapshot
  }

  override def receiveCommand: Receive = {
    case Cmd(data) =>
      val evt = Evt(s"$data-$numEvents")
      val newEvent = Evt(s"$data-${ numEvents+1 }")
      //persist(evt)(evt => updateState(evt))
      persist(evt)(updateState)
      persist(newEvent)(updateState)
      sender ! evt
      sender ! newEvent
    case Snap => saveSnapshot(state)
    case SaveSnapshotSuccess(metadata) => log.info(s"SaveSapshotSuccess: metadata=$metadata")
    case SaveSnapshotFailure(metadata,reason) => log.error(s"SaveSapshotSuccess: metadata=$metadata,reason=$reason")
    case Print =>
      println(state)
      sender ! State(state.toString)
    case Boom => throw new Exception("Boom")


  }


}
