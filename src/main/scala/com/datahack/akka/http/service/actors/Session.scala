package com.datahack.akka.http.service.actors

import akka.actor.{Actor, ActorRef, PoisonPill}
import com.datahack.akka.http.model.dtos.Order
import com.datahack.akka.http.service.actors.Inventory._
import com.datahack.akka.http.service.actors.Session._

import scala.collection.mutable

/*
 * Los mensajes que maneja este actor
 */
case object Session {
  case class ProcessOrder(order: Order, sender: ActorRef) // Le indica que tiene que atender y procesar una orden de compra
  case class OrderProcessed(sessionId: String, order: Order) // Contesta con este mensaje indicando que la orcen de compra se ha procesado
  case class OrderAlreadyBeingProcessed(requestId: String) // Contesta con este mensaje cuando ya hay una orden de compra procesada con ese id de petición
  case class Checkout(sender: ActorRef) // Indica al actor que tiene que hacer un checkout de su sesión
  case class CancelSession(sender: ActorRef) // Indica al actor que tiene que cancelar y limpiar su sessión
  case class SessionFinished(sessionId: String) // Responde con este mensaje cuando a terminado de limpiar una sesión
}

// Recive la referencia al actor que gestiona el inventario de productos
class Session(inventory: ActorRef, sessionId: String) extends Actor {

  // Mapa para registrar las peticiones de compra junto con la referencia al actor que hizo la petición para poder contestarle
  // La clave es el identificador de la petición de orden de compra
  var itemsProcessed: mutable.Map[String, (Order, ActorRef)] = mutable.Map.empty[String, (Order, ActorRef)]
  // Mapa con todos los productos procesados durante la sesión. La clave es el id del producto
  var shoppingCart: mutable.Map[Long, Order] = mutable.Map.empty[Long, Order]

  // Recoge la referencia al actor que indicó que se cancelase o se hiciera el checkout de la sesión para
  // poder contestarle
  var cancelOrCheckoutSender: Option[ActorRef] = None

  // TODO: iniciar el actor con el comportamiento normal
  override def receive: Receive = ???

  // TODO: este actor tiene dos comportamientos. El inicial es el comportamiento normal que espera procesar
  // órdenes de compra y las respuestas del actor de inventario
  def normalBehaviour: Receive = {
    case ProcessOrder(order, sender) =>
      // TODO: comprobar si la orden de compra ya se ha procesado
      // Si es así contestar que ya se ha procesado
      // Sino añadir la orden de compra a la colección de items procesados y procesar la compra con el
      // actor de inventario

    case NotEnoughProductLeft(requestId) =>
      // TODO: el actor de inventario nos indica que no hay suficientes unidades para stisfacer la orden de compra
      // Indicar al actor que hizo la orden de compra que no queda suficiente producto

    case ProductNotFound(requestId) =>
      // TODO: el actor de inventario nos indica que no existe el producto indicado en la orden de compra
      // Indicar al actor que hizo la orden de compra que no existe ese producto

    case ReservedProduct(requestId, amount) =>
      // TODO: el actor de inventario nos indica que ha procesado la compra.
      // añadir al carrito de la compra el producto gestionado
      // Informar al actor que hizo la orden de compra de que el producto ya ha sido añadido a la sesión

    case CannotProcessOder(requestId) =>
      // TODO: el actor de invenrario nos indica que no puede procesar las ordenes de compra porque está iniciándose
      // Informar al actor que hizo la petición de compra de que no se puede procesar su orden

    case Checkout(requestActor) =>
      // TODO: se pide hacer un checkout de la sesión
      // actualizar el valor de la variable cancelOrCheckout con la referencia al actor que hizo la petición
      // cambiar de comportamiento al actor al comportamiento de checkout
      // indicar al actor de invenario que persista el carrito de la sesión

    case CancelSession(requestActor) =>
      // TODO: le indica al actor que tiene que cacelar la sesión
      // actualizar el valor de la variable cancelOrCheckout con la referencia al actor que hizo la petición
      // cambiar de comportamiento al actor al comportamiento de checkout
      // indicar al actor de inventario que reestablezca el inventario con los productos del carrito de la sesión

  }

  // TODO: el segundo comportamiento del actor es aquel en el que el actor se encuentra cuando está
  // procesando un checkout o una cancelación de la sesión
  def checkOutOrCancelBehaviour: Receive = {
    case SessionCheckedOut =>
      // TODO: el actor de inventario nos indica que ha terminado de hacer el checkout de la sesión
      // informamos al actor que nos hizo la petición con el mismo mensaje que el actor de inventario
      // matamos al actor de la sesión

    case SessionRestored =>
      // TODO: el actor de inventario nos indica que ha terminado de limpiar la sesión
      // informamos al actor que nos hizo la petición con el mismo mensaje que el actor de inventario
      // matamos al actor de la sesión

    case _ =>
      // TODO: no se admiten otro tipo de mensajes. Se informa al actor que envía el mensaje que la sesión
      // está terminada
  }
}
