package com.datahack.akka.http.service.actors

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{EventFilter, TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{Order, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.actors.Inventory._
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class InventorySpec
  extends TestKit(ActorSystem(
    "InventorySpec",
    ConfigFactory.parseString(
      """akka.loggers = ["akka.testkit.TestEventListener"]
        |akka.test.filter-leeway = 30000
      """.stripMargin)))
    with WordSpecLike
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))
  val inventory: Map[Long, (Product, Float)] =
    products.map(product => (product.id.get, (product, product.units))).toMap[Long, (Product, Float)]
  lazy val productDao = new ProductDao
  lazy val productService = new ProductService(productDao)

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Inventory Actor" should {

    "create inventory from database when receive InitInventory message" in {

    }

    "response with CannotProcessOder when receive OrderItem and the behaviour of the actor is bootstrapBehaviour" in {

    }

    "response with ReservedProduct after actualize inventory" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val inventoryRef: TestActorRef[Inventory] = TestActorRef[Inventory](new Inventory(productService))
      val requestId = UUID.randomUUID().toString
      val productId = products.head.id.get
      val productQuantity = products.head.units
      val behavior: Inventory#Receive = inventoryRef.underlyingActor.manageOrdersBehaviour

      inventoryRef.underlyingActor.inventory = collection.mutable.Map(inventory.toSeq: _*)
      inventoryRef.underlyingActor.context.become(behavior)

      inventoryRef ! OrderItem(requestId, productId, productQuantity)

      sender.expectMsg(ReservedProduct(requestId, products.head.price * productQuantity))
      inventoryRef.underlyingActor.inventory.get(productId).get._2 shouldBe 0
    }

    "response with NotEnoughProductLeft when there are not enough quantity of product at the inventory" in {

    }

    "response with ProductNotFound when the product not exist at the inventory" in {

    }

    "persist inventory into database when receive PersistSession message" in {

    }

    "restore inventory after clear a session" in {

    }

  }

}
