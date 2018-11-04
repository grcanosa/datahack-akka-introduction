package com.datahack.akka.http.model.daos

import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ProductDaoSpec
  extends WordSpec
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))
  lazy val productDao = new ProductDao

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Product DAO" should {

    "get all stored products" in {
      val result = Await.result(productDao.getAll, 5 seconds)
      result should have length products.length
    }

    "search a product by id" in {
      val Some(result) = Await.result(productDao.getById(products.head.id.get), 5 seconds)
      result shouldBe products.head
    }

    "get none if the product that we are searching by id it does not exist into database" in {
      val result = Await.result(productDao.getById(products.length + 10), 5 seconds)
      result shouldBe None
    }

    "insert a product into database" in {
      val prductToInsert: Product = genProduct.sample.get.copy(id = None)
      val id = Await.result(productDao.insert(prductToInsert), 5 seconds)

      val productToInsertWithId = prductToInsert.copy(id = Some(id))
      val Some(productStored) = Await.result(SqlTestUtils.findEntity(productToInsertWithId, schemaName), 5 seconds)
      productStored shouldBe productToInsertWithId
    }

    "update a product stored into database" in {
      val productToUpdate: Product = products.last.copy(name = "TheNewName")

      Await.result(productDao.update(productToUpdate), 5 seconds)

      val Some(productUpdated) = Await.result(SqlTestUtils.findEntity(productToUpdate, schemaName), 5 seconds)
      productUpdated shouldBe productToUpdate
    }

    "delete a product stored into database" in {
      val productToDelete = products.head
      val result = Await.result(productDao.delete(productToDelete.id.get), 5 seconds)
      result shouldBe 1
      val productStored = Await.result(SqlTestUtils.findEntity(productToDelete, schemaName), 5 seconds)
      productStored shouldBe None
    }
  }

  override protected def afterAll(): Unit = {
    Await.result(SqlTestUtils.dropDatabase(schemaName), 5 seconds)
  }
}
