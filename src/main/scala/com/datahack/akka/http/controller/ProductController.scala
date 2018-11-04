package com.datahack.akka.http.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import akka.pattern.ask
import com.datahack.akka.http.controller.actors.ProductControllerActor._
import com.datahack.akka.http.model.dtos.{JsonSupport, Product}
import com.datahack.akka.http.service.ProductService._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ProductController (productActor: ActorRef)
                        (implicit executionContext: ExecutionContext) extends Directives with JsonSupport {

  implicit val timeout: Timeout = Timeout(60 seconds)

  val routes: Route = getAllProducts ~ getProduct ~ insertProduct ~ updateProduct ~ deleteProduct

  def getAllProducts: server.Route =
    path("products") {
      get {
        onSuccess(productActor ? GetAllProducts) {
          case AllProducts(products:Seq[Product]) => complete(products)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

  def getProduct: server.Route =
    path("products" / LongNumber) { productId =>
      get {
        onSuccess(productActor ? SearchProduct(productId)) {
          case FoundProduct(product: Product) => complete(product)
          case ProductNotFound => complete(StatusCodes.NotFound)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

  def insertProduct: server.Route =
    path("products") {
      post {
        entity(as[Product]) { product =>
          onSuccess(productActor ? CreateProduct(product)) {
            case StoredProduct(product) => complete(product)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

  def updateProduct: server.Route =
    path("products" / LongNumber) { productId =>
      put {
        entity(as[Product]) { product =>
          onSuccess(productActor ? UpdateProduct(product.copy(id = Some(productId)))) {
            case UpdatedProduct(product) => complete(product)
            case ProductNotFound => complete(StatusCodes.NotFound)
            case _ => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

  def deleteProduct: server.Route =
    path("products" / LongNumber) { productId =>
      delete {
        onSuccess(productActor ? DeleteProduct(productId)) {
          case ProductDeleted => complete(StatusCodes.OK)
          case ProductNotFound => complete(StatusCodes.NotFound)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }

}