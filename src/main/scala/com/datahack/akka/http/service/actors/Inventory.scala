package com.datahack.akka.http.service.actors

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import com.datahack.akka.http.model.dtos.{Order, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.ProductService.AllProducts
import com.datahack.akka.http.service.actors.Inventory._

import scala.collection.mutable
import scala.concurrent.ExecutionContext

// Estos son los mensajes que va ha manejar este actor:
object Inventory {
  case object InitInventory // Le indica al actor que tiene que genera su inventario
  case class OrderItem(itemRequestId: String, productId: Long, quantity: Float) // Mensaje para atender una orden de compra
  case class ProductNotFound(itemRequestId: String) // Respuesta del actor cuando no encuetra el producto solicitado
  case class NotEnoughProductLeft(itemRequestId: String) // Respuesta del actor cuando no queda suficiente cantidad de producto para atender la demanda
  case class ReservedProduct(itemRequestId: String, amount: Float) // Respuesta para indicar que la orden de compra ha sido procesada y la cantidad de producto reservada
  case class CannotProcessOder(itemRequestId: String) // Respuesta cuando el acotor se esta iniciando y creando su inventario y no puede atender peticiones de compra
  case class PersistSession(items: Seq[Order]) // Le indica al actor que se ha hecho checkout de la sesión y hay que materializar los cambios para esa sesión en base de datos
  case class ClearSession(items: Seq[Order]) // Le indica al actor que se va ha cancelar la sesión y hay que actualizar el inventario restaurando los valores de los productos asociados a las ordenes de compra de la sesión
  case object SessionRestored // Respuesta cuando se ha limpiado las sesión y se ha restaurado el inventario
  case object SessionCheckedOut // Respuesta cuando se ha materializado el inventario en base de datos
}

class Inventory(productService: ProductService) extends Actor with ActorLogging {

  implicit val executionContext: ExecutionContext = context.dispatcher

  // Vamos a mantener un mapa mutable para gesionar el invenario
  var inventory: mutable.Map[Long, (Product, Float)] = mutable.Map.empty[Long, (Product, Float)]

  // TODO: El comportamiento inicial será el que nos permita iniciar el inventario
  override def receive: Receive = ???

  // Vamos a tener dos comportamientos:
  // Un comportamiento de inicio mientras obtenemos los datos de los productos de la base de datos
  def bootstrapBehaviour: Receive = {
    case InitInventory =>
      // TODO: cuando nos llege el mensaje de iniciar el inventario, rellenaremos el mapa de inventario con
      // los datos de los productos almacenados en la base de datos.
      log.debug("Getting product list form database")

    case AllProducts(products) =>
      // Una vez que hemos procesado todos los productos y hemos generado el inventario cambiamos el comportamiento
      // del actor para procesar ordenes de compra
      log.info(s"Constructing inventory with ${products.length} products")

    case OrderItem(itemRequestId, _, _) =>
      // TODO: Si recivimos una petición para procesar una orden de compra, la ignoramos y contestamos
      // que no podemos processar la orden
      log.debug("Cannot process order, inventory under construction")
  }

  // Un segundo comportamiento para procesar las peticiones de las sesiones
  // al que cambiaremos despúes de inicializar el inventario
  def manageOrdersBehaviour: Receive = {

    case OrderItem(itemRequestId, productId, quantity) =>
      // TODO: cuando se recive una orden de compra con item concreto, procesamos la orden, para ello buscaremos
      // en el inventario el producto por su id. Si no existe contestamos indicando que el producto no se ha encontrado
      // si existe, miramos a ver si hay suficiente cantidad para atender la orden de compra.
      // Si hay suficiente cantidad de producto contenstamos que se ha reservado el producto y cuanto asciende
      // el precio del pedido. Si no hay producto suficiente contestamos que no queda suficiente producto.

    case PersistSession(items) =>
      // TODO: Si recivimos la orden de persistir las sessión, iteramos sobre los items de compra y descontamos su
      // cantidad de la cantidad almacenada en la base de datos. Una vez terminado el proceso contestaremos indicacndo
      // que se ha terminado el proceso de checkout.

    case ClearSession(items) =>
      // TODO: si recivimos la orden de limpiar una sesión, recorreremos los items y reestableceremos la cantidad
      // disponible en el inventario. Una vez terminado el proceso respondemos indicando que se han reestablecido
      // los valores al estado anterior de la sesión cancelada.

  }
}
