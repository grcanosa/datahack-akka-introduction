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

  def products()(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] =
    productDao.getAll.map(AllProducts)

  def searchProduct(id: Long)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] =
    productDao.getById(id).map(_.map(FoundProduct).getOrElse(ProductNotFound))

  def insertProduct(product: Product)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = for {
    id <- productDao.insert(product)
    product <- productDao.getById(id)
  } yield StoredProduct(product)

  def updateProduct(product: Product)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = {
    val result = for {
      productFound <- productDao.getById(product.id.get)
      if productFound.isDefined
      _ <- productDao.update(product)
    } yield UpdatedProduct(product)

    result.recover { case _: NoSuchElementException => ProductNotFound }
  }

  def deleteProduct(id: Long)(implicit executionContext: ExecutionContext): Future[ProductServiceResponse] = {
    val result = for {
      productFound <- productDao.getById(id)
      if productFound.isDefined
      _ <- productDao.delete(id)
    } yield { ProductDeleted }

    result.recover { case _: NoSuchElementException => ProductNotFound }
  }

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