package com.datahack.akka.http.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import akka.pattern.ask
import com.datahack.akka.http.controller.actors.SessionControllerActor._
import com.datahack.akka.http.model.dtos.{JsonSupport, Order}
import com.datahack.akka.http.service.actors.Inventory.{NotEnoughProductLeft, ProductNotFound, ReservedProduct, SessionCheckedOut}
import com.datahack.akka.http.service.actors.Session.{OrderAlreadyBeingProcessed, OrderProcessed, SessionFinished}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

// TODO: recive la referencia del actor sessionControllerActor por referencia
// implícitamente recive e execution context
// es necesario que extienda de los traits necesarios de akka http y para gestionar los marshal y unmarshal de los JSON
class SessionController(sessionControllerActor: ActorRef)
                       (implicit executionContext: ExecutionContext) extends Directives with JsonSupport {

  // Timeout de las peticiones del controlador
  implicit val timeout: Timeout = Timeout(60 seconds)

  // TODO: Añadir las rutas del controlador
  val routes: Route = addOrderAndCreateSession ~ addOrderToSession ~ checkoutSession ~ cancelSession

  // TODO: método POST /session  => se crea la sesión y se procesa la orden de compra
  // si todo ha ido bien se informa con el id de la sesión
  def addOrderAndCreateSession: Route =
    path("session") {
      post {
        entity(as[Order]) { order =>
          onSuccess(sessionControllerActor ? AddOrderToSession(None, order)) {
            case OrderProcessed(sessionId, _) => complete(sessionId)
            case OrderAlreadyBeingProcessed(_) => complete(StatusCodes.AlreadyReported)
            case NotEnoughProductLeft(_) => complete(StatusCodes.UnprocessableEntity)
            case ProductNotFound(_) => complete(StatusCodes.NotFound)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

  // TODO: método PUT /session/UUID en el body va la orden de compra => si la sesión existe se procesa la orden de compra
  // si todo ha ido bien se devuelve la orden de compra
  // si la sesión no existe se informa con un 404
  // si el producto no existe se informa con un 404
  def addOrderToSession: Route =
    path("session" / JavaUUID) { sessionId =>
      put {
        entity(as[Order]) { order =>
          onSuccess(sessionControllerActor ? AddOrderToSession(Some(sessionId.toString), order)) {
            case OrderProcessed(_, order) => complete(order)
            case OrderAlreadyBeingProcessed(_) => complete(StatusCodes.AlreadyReported)
            case NotEnoughProductLeft(_) => complete(StatusCodes.UnprocessableEntity)
            case ProductNotFound(_) => complete(StatusCodes.NotFound)
            case SessionNotFound => complete(StatusCodes.NotFound)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

  // TODO: método GET /session/UUID/checkout => se procesa el checkout de la sesión
  // si todo va bien se informa con un 200
  // si la sesión no existe se informa con un 404
  def checkoutSession: Route =
    path("session" / JavaUUID / "checkout") { sessionId =>
      get {
        onSuccess(sessionControllerActor ? FinishSession(sessionId.toString)) {
          case SessionCheckedOut => complete(StatusCodes.OK)
          case SessionNotFound => complete(StatusCodes.NotFound)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }
  // TODO: método DELETE /session/UUID => se cancela la sesión
  // si todo va bien se informa con un 200
  // si la sesión no existe se informa con un 404
  def cancelSession: Route =
    path("session" / JavaUUID) { sessionId =>
      delete {
        onSuccess(sessionControllerActor ? RemoveSession(sessionId.toString)) {
          case SessionFinished => complete(StatusCodes.OK)
          case SessionRemoved => complete(StatusCodes.OK)
          case SessionNotFound => complete(StatusCodes.NotFound)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

}
