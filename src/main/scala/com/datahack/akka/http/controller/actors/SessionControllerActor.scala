package com.datahack.akka.http.controller.actors

import java.util.UUID

import akka.actor.{Actor, ActorRef, Props}
import com.datahack.akka.http.controller.actors.SessionControllerActor.{AddOrderToSession, FinishSession, RemoveSession, SessionNotFound}
import com.datahack.akka.http.model.dtos.Order
import com.datahack.akka.http.service.actors.Session
import com.datahack.akka.http.service.actors.Session.{CancelSession, Checkout, ProcessOrder, SessionFinished}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

// Los mensajes que gestiona el controlador
object SessionControllerActor {
  case class AddOrderToSession(sessionId: Option[String], order: Order) // gestionar en la sesión una nueva orden de compra
  case class RemoveSession(sessionId: String) // Indica cancelar la sesión
  case class FinishSession(sessionId: String) // Indica hacer un checkout de la sesión
  case object SessionRemoved // contesta con este mensaje indicando que la sesión se ha cancelado
  case object SessionNotFound // contesta con este mensaje indicando que la sesión no se encontró porque no existe
}

// TODO: recive por parámetro la referencia al actor de inventario
class SessionControllerActor extends Actor {

  implicit val executionContext: ExecutionContext = context.dispatcher

  // Mapa que contiene las sesiones abiertas en la aplicación. La clave es el identificador de la sesión y su valor
  // la referencia al actor que gestiona la sesión
  var sessions: mutable.Map[String, ActorRef] = mutable.Map.empty[String, ActorRef]

  override def receive: Receive = {
    case AddOrderToSession(sessionId, order) =>
      // TODO: si tenemos sessionId en el mensaje:
      //      Comprobamos que la sesión existe. Si es así le indicamos que procese el oren de compra
      //      Sino existe le indicamos al controlador que lo esiste una sesión con ese id
      // Si no tenemos sessionId en el mensaje, entonces creamos un nuevo actor de sesión, dándole un sessionId
      // lo añadimos al mapa de sesiones y le indicamos a la sesión que procese el orden de compra

    case FinishSession(sessionId: String) =>
      // TODO: Nos indica que tenemos que hacer el checkout de la sesión
      // buscamos la sesión, si existe le indicamos que haga el checkout y la borramos del mapa
      // si no existe informamos al controlador

    case RemoveSession(sessionId: String) =>
      // TODO: Nos indica que tenemos que cancelar la sesión
      // buscamos la sesión, si existe le indicamos que limpie la sesión y la borramos del mapa
      // si no existe informamos al controlador

    case SessionFinished(sessionId) =>
      // TODO: No indica que ha ocurrido un timeout de la sesión.
      // buscamos en el mapa la sesión le indicamos que a limpie y la borramos del mapa

  }
}
