package com.datahack.akka.http.service

import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.service.ProductService._

import scala.concurrent.{ExecutionContext, Future}

/*
 * Mensajes de respuesta de los métodos del servicio de productos
 */
object ProductService {

  trait ProductServiceResponse
  case class AllProducts(products: Seq[Product]) extends ProductServiceResponse
  case class FoundProduct(product: Product) extends ProductServiceResponse
  case object ProductNotFound extends ProductServiceResponse
  case class StoredProduct(product: Option[Product]) extends ProductServiceResponse
  case class UpdatedProduct(product: Product) extends ProductServiceResponse
  case object ProductDeleted extends ProductServiceResponse
  case class InventoryPersisted(itemsStored: Int) extends ProductServiceResponse
}

class ProductService(productDao: ProductDao) {

  def products()(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = ???

  def searchProduct(id: Long)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = ???

  def insertProduct(product: Product)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = ???

  def updateProduct(product: Product)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = ???

  def deleteProduct(id: Long)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = ???

  def persistSession(items: Seq[(Long, Float)])(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = {
    for {
      products <- Future.sequence(items.map { item =>
                    productDao.getById(item._1).map { product =>
                      product.map(p => p.copy(units = p.units - item._2))
                    }
                  })
      if products.flatten.length == items.length
      response <- Future.sequence(products.flatten.map(product => productDao.update(product)))
    } yield { InventoryPersisted(response.sum) }
  }

}