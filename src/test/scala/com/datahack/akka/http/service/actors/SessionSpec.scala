package com.datahack.akka.http.service.actors

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{Order, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.actors.Inventory.{NotEnoughProductLeft, ProductNotFound, ReservedProduct, SessionCheckedOut}
import com.datahack.akka.http.service.actors.Session._
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class SessionSpec
  extends TestKit(ActorSystem(
    "SessionSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
    with WordSpecLike
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  // La clase de SQLTestUtils nos devolverá en nombre del esquema de la base de datos al crearlo para el test
  var schemaName: String = ""

  // Generamos productos para popular la base de datos para los tests
  val products: Seq[Product] = (1 to 5).map(i => genProduct.sample.get.copy(id = Some(i)))

  // Creamos el inventory con los productos generados
  val inventory: Map[Long, (Product, Float)] =
    products.map(product => (product.id.get, (product, product.units))).toMap[Long, (Product, Float)]

  // creamos las clases necesarias para los test
  lazy val productDao = new ProductDao
  lazy val productService = new ProductService(productDao)

  override protected def beforeAll(): Unit = {
    // Creamos el esquema de base datos
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    // Insertamos los productos en la base dedatos
    Await.result(Future.sequence(SqlTestUtils.insertList(products.toList, schemaName)), 5 seconds)
  }

  "Session Actor" should {

    "process an order and response to the sender that the order was processed" in {
      val inventoryRef: TestActorRef[Inventory] = TestActorRef[Inventory](new Inventory(productService))
      val sessionId: String = UUID.randomUUID().toString
      val sessionRef: TestActorRef[Session] = TestActorRef[Session](new Session(inventoryRef, sessionId))
      val probe = TestProbe()
      val requestId = UUID.randomUUID().toString
      val orderProduct = products.head
      val order = Order(Some(requestId), orderProduct.id.get, orderProduct.units)
      val behavior: Inventory#Receive = inventoryRef.underlyingActor.manageOrdersBehaviour

      inventoryRef.underlyingActor.inventory = collection.mutable.Map(inventory.toSeq: _*)
      inventoryRef.underlyingActor.context.become(behavior)

      sessionRef ! ProcessOrder(order, probe.ref)

      probe.expectMsg(OrderProcessed(sessionId, order))
      inventoryRef.underlyingActor.inventory.get(order.productId).get._2 shouldBe 0
    }

    "process an order and return NotEnoughProductLeft when there is no enough product units" in {

    }

    "process an order and return ProductNotFound message when the product does not exist in the inventory" in {

    }

    "return OrderAlreadyBeingProcessed when receive a process order that it is already processed" in {

    }

    "return SessionCheckedOut when it is requested an already done" in {

    }

    "return SessionFinished when it is requested an already done" in {

    }
  }

}
