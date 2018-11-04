package com.datahack.akka.http.controller

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestActorRef
import com.datahack.akka.http.controller.actors.ProductControllerActor
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{JsonSupport, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ProductControllerSpec
  extends WordSpec
    with Matchers
    with Directives
    with ScalatestRouteTest
    with BeforeAndAfterAll
    with Generators
    with JsonSupport {

  var schemaName: String = ""

  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))
  lazy val productDao: ProductDao = new ProductDao
  lazy val productService: ProductService = new ProductService(productDao)
  lazy val productControllerActor: TestActorRef[ProductControllerActor] =
    TestActorRef[ProductControllerActor](new ProductControllerActor(productService))
  lazy val productController: ProductController = new ProductController(productControllerActor)

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Product Controller" should {

    "get all products offered in the application" in {
      Get("/products") ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Product]]
        response.length shouldBe products.length
      }
    }

    "get specific product offered by it id" in {
      Get(s"/products/${products.head.id.get}") ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Product]
        response shouldBe products.head
      }
    }

    "get not found status code when searching a product that is not offered" in {
      Get(s"/products/${products.length + 20}") ~> productController.routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "add a new prdouct" in {
      val productToStore = genProduct.sample.get

      Post("/products").withEntity(
        ContentTypes.`application/json`,
        productToStore.toJson
      ) ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Product]
        response.producer shouldBe productToStore.producer
        response.name shouldBe productToStore.name
        response.description shouldBe productToStore.description
        response.price shouldBe productToStore.price
        response.units shouldBe productToStore.units

        val Some(productFound:Product) = Await.result(SqlTestUtils.findEntity(response, schemaName), 5 seconds)

        productFound.producer shouldBe productToStore.producer
        productFound.name shouldBe productToStore.name
        productFound.description shouldBe productToStore.description
        productFound.price shouldBe productToStore.price
        productFound.units shouldBe productToStore.units
      }
    }

    "update the product data" in {
      val productToUpdate = products.last.copy(name = "The new name")

      Put(s"/products/${productToUpdate.id.get}").withEntity(
        ContentTypes.`application/json`,
        productToUpdate.toJson
      ) ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Product]
        response shouldBe productToUpdate
      }

      val Some(productFound) = Await.result(SqlTestUtils.findEntity(productToUpdate, schemaName), 5 seconds)

      productToUpdate shouldBe productFound
    }

    "get Not Found status code when trying to update a product that is not offered" in {
      val productToUpdate = products.last.copy(id = Some(products.length + 20), name = "The new name")

      Put(s"/products/${productToUpdate.id.get}").withEntity(
        ContentTypes.`application/json`,
        productToUpdate.toJson
      ) ~> productController.routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "delete an offered product" in {
      val productToDelete = genProduct.sample.get
      val id = Await.result(SqlTestUtils.insert(productToDelete, schemaName), 5 seconds)

      Delete(s"/products/$id") ~> productController.routes ~> check {
        status shouldBe StatusCodes.OK
      }

      val result = Await.result(SqlTestUtils.findEntity(productToDelete, schemaName), 5 seconds)
      result shouldBe None
    }

    "get Not Found status code when trying to delete a product not offered" in {
      Delete(s"/products/${products.length + 30}") ~> productController.routes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
