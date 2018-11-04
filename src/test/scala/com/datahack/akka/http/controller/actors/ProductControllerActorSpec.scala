package com.datahack.akka.http.controller.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.controller.actors.ProductControllerActor._
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.ProductService._
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class ProductControllerActorSpec
  extends TestKit(ActorSystem("ProductControllerActorSpec"))
    with WordSpecLike
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))
  lazy val productDao = new ProductDao
  lazy val productService: ProductService = new ProductService(productDao)
  lazy val productControllerActor: TestActorRef[ProductControllerActor] =
    TestActorRef[ProductControllerActor](new ProductControllerActor(productService))

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Product Controller Actor" should {

    "get all product products" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      productControllerActor ! GetAllProducts

      sender.expectMsg(AllProducts(products))
    }

    "search a product by id" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      productControllerActor ! SearchProduct(products.head.id.get)

      sender.expectMsg(FoundProduct(products.head))
    }

    "get ProductNotFound message if the product that we are searching by id it does not exist into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      productControllerActor ! SearchProduct(products.length + 10)

      sender.expectMsg(ProductNotFound)
    }

    "insert a product into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val productToInsert: Product = genProduct.sample.get.copy(id = None)

      productControllerActor ! CreateProduct(productToInsert)

      val productStored: StoredProduct = sender.expectMsgType[StoredProduct]

      val Some(productFound) = Await.result(SqlTestUtils.findEntity(productStored.product.get, schemaName), 5 seconds)

      productStored.product.get shouldBe productFound
    }

    "update a product stored into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val productToUpdate: Product = products.last.copy(name = "TheNewName")

      productControllerActor ! UpdateProduct(productToUpdate)

      sender.expectMsg(UpdatedProduct(productToUpdate))

      val Some(productUpdated) = Await.result(SqlTestUtils.findEntity(productToUpdate, schemaName), 5 seconds)
      productUpdated shouldBe productToUpdate
    }

    "get ProductNotFound message when trying to update a product that not exit into database" in  {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val productToUpdate: Product = products.last.copy(id = Some(products.length + 20), name = "TheNewName")

      productControllerActor ! UpdateProduct(productToUpdate)

      sender.expectMsg(ProductNotFound)
    }

    "delete a product stored into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val productToDelete = products.head

      productControllerActor ! DeleteProduct(productToDelete.id.get)

      sender.expectMsg(ProductDeleted)

      val productStored = Await.result(SqlTestUtils.findEntity(productToDelete, schemaName), 5 seconds)
      productStored shouldBe None
    }

    "get ProductNotFound message whe trying to delete a product that not exits" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      productControllerActor ! DeleteProduct(products.length + 20)

      sender.expectMsg(ProductNotFound)

    }

  }

  override protected def afterAll(): Unit = {
    Await.result(SqlTestUtils.dropDatabase(schemaName), 5 seconds)
  }
}
