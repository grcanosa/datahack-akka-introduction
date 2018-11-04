package com.datahack.akka.http.controller.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.datahack.akka.http.controller.actors.ProductControllerActor._
import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.service.ProductService

import scala.concurrent.ExecutionContextExecutor

object ProductControllerActor {
  case object GetAllProducts
  case class SearchProduct(id: Long)
  case class CreateProduct(product: Product)
  case class UpdateProduct(product: Product)
  case class DeleteProduct(id: Long)
}

// TODO: El actor recive por parámetro el servicio que gestiona los procutos
class ProductControllerActor (productService: ProductService) extends Actor {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {

    //TODO: utilizar los métodos del servicio para getionar los mensajes y devolver los datos al controlador
    case GetAllProducts => productService.products pipeTo sender
    case SearchProduct(id) => productService.searchProduct(id) pipeTo sender
    case CreateProduct(product) => productService.insertProduct(product) pipeTo sender
    case UpdateProduct(product) => productService.updateProduct(product) pipeTo sender
    case DeleteProduct(id) => productService.deleteProduct(id) pipeTo sender
  }
}