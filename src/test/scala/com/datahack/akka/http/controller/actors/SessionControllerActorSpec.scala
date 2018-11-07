package com.datahack.akka.http.controller.actors

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.controller.actors.SessionControllerActor.{AddOrderToSession, FinishSession, RemoveSession, SessionNotFound}
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{Order, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.actors.Inventory.{ReservedProduct, SessionCheckedOut}
import com.datahack.akka.http.service.actors.Session.{OrderProcessed, SessionFinished}
import com.datahack.akka.http.service.actors.{Inventory, Session}
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class SessionControllerActorSpec
  extends TestKit(ActorSystem(
    "SessionControllerActorSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
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

  "Session controller actor" should {

    "create a new session and process order request" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val inventoryRef: TestActorRef[Inventory] = TestActorRef[Inventory](new Inventory(productService))
      val sessionControllerRef: TestActorRef[SessionControllerActor] =
        TestActorRef[SessionControllerActor](new SessionControllerActor(inventoryRef))
      val orderProduct = products.head
      val order = Order(None, orderProduct.id.get, orderProduct.units)
      val behavior: Inventory#Receive = inventoryRef.underlyingActor.manageOrdersBehaviour

      inventoryRef.underlyingActor.inventory = collection.mutable.Map(inventory.toSeq: _*)
      inventoryRef.underlyingActor.context.become(behavior)

      sessionControllerRef ! AddOrderToSession(None, order)

      sender.expectMsgType[OrderProcessed]
      sessionControllerRef.underlyingActor.sessions.size shouldBe 1
    }

    "process order request into session indicated" in {

    }

    "get SessionNotFound message when trying to process order request into session that not exist" in {

    }

    "finish a session and checkout its items into database" in {

    }

    "cancel a session and clear its items into inventory" in {

    }
  }

}
