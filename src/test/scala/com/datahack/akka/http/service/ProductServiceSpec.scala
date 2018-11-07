package com.datahack.akka.http.service

import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.service.ProductService.{ProductDeleted, _}
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ProductServiceSpec
  extends WordSpec
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))
  lazy val productDao = new ProductDao
  lazy val productService = new ProductService(productDao)

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Product Service" should {

    "get all stored products" in {
      Await.result(productService.products, 5 seconds) match {
        case result :AllProducts =>
          result.products should have length products.length
        case _ => fail("AllProduct response expected")
      }
    }

    "search a product by id" in {
      Await.result(productService.searchProduct(products.head.id.get), 5 seconds) match {
        case result :FoundProduct =>
          result.product shouldBe products.head
        case _ => fail("FoundProduct response expected")
      }
    }

    "get none if the product that we are searching by id it does not exist into database" in {
      Await.result(productService.searchProduct(products.length + 10), 5 seconds) match {
        case result :ProductNotFound.type => succeed
        case _ => fail("ProductNotFound response expected")
      }
    }

    "insert a product into database" in {
      val prductToInsert: Product = genProduct.sample.get.copy(id = None)
      Await.result(productService.insertProduct(prductToInsert), 5 seconds) match {
        case result :StoredProduct =>
          val Some(productStored) = Await.result(SqlTestUtils.findEntity(result.product.get, schemaName), 5 seconds)
          productStored shouldBe result.product.get
        case _ => fail("StoredProduct response expected")
      }
    }

    "update a product stored into database" in {
      val productToUpdate: Product = products.last.copy(name = "TheNewName")

      Await.result(productService.updateProduct(productToUpdate), 5 seconds) match {
        case result :UpdatedProduct =>
          val Some(productUpdated) = Await.result(SqlTestUtils.findEntity(result.product, schemaName), 5 seconds)
          productUpdated shouldBe result.product
        case _ => fail("UpdatedProduct response expected")
      }
    }

    "delete a product stored into database" in {
      val productToDelete = genProduct.sample.get
      val id = Await.result(SqlTestUtils.insert(productToDelete, schemaName), 5 seconds)
      Await.result(productService.deleteProduct(id), 5 seconds)  match {
        case result :ProductDeleted.type =>
          val productStored = Await.result(SqlTestUtils.findEntity(productToDelete.copy(id = Some(id)), schemaName), 5 seconds)
          productStored shouldBe None
        case _ => fail("ProductDeleted response expected")
      }
    }

    /*"persist the session changes into database" in {
      val updatedProducts = products
        .map(product => product.copy(units = product.units - (product.units / 2)))
      val session = updatedProducts.map(p => (p.id.get, p.units))
      Await.result(productService.persistSession(session), 5 seconds) match {
        case result: InventoryPersisted =>
          result.itemsStored shouldBe updatedProducts.length
          val updatedItems = Await.result(Future.sequence(updatedProducts.map(SqlTestUtils.findEntity(_, schemaName))), 10 seconds)
          updatedItems.asInstanceOf[Seq[Option[Product]]].flatten shouldBe updatedProducts
        case _ => fail("InventoryPersisted response expected")
      }
    }*/
  }

  override protected def afterAll(): Unit = {
    Await.result(SqlTestUtils.dropDatabase(schemaName), 5 seconds)
  }

}
