package com.datahack.akka.http.controller

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestActorRef
import com.datahack.akka.http.controller.actors.SessionControllerActor
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{JsonSupport, Order, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.actors.{Inventory, Session}
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json._

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class SessionControllerSpec
  extends WordSpec
    with Matchers
    with Directives
    with ScalatestRouteTest
    with BeforeAndAfterAll
    with Generators
    with JsonSupport {

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

  "Session Controller" should {

    "create a session and add order to it" in {
      val inventoryRef: TestActorRef[Inventory] = ???
      val sessionControllerActorRef: TestActorRef[SessionControllerActor] =
        TestActorRef[SessionControllerActor](new SessionControllerActor(inventoryRef))
      val sessionController = new SessionController()

      val productToOrder = products.head
      val order = Order(None, productToOrder.id.get, productToOrder.units)
      val behavior: Inventory#Receive = inventoryRef.underlyingActor.manageOrdersBehaviour

      inventoryRef.underlyingActor.inventory = collection.mutable.Map(inventory.toSeq: _*)
      inventoryRef.underlyingActor.context.become(behavior)

      Post("/session").withEntity(
        ContentTypes.`application/json`,
        order.toJson
      ) ~> sessionController.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[String] shouldBe sessionControllerActorRef.underlyingActor.sessions.toList.head._1
      }
    }

    "get 404 Not found when trying to order a product that does not exist into inventory" in {

    }

    "add a order to a existing session" in {

    }

    "get 404 NotFound error when trying to add an order into a session that not exist" in {

    }

    "get 404 NotFound error when trying to add an order of a product that not exist into a session" in {

    }

    "get 200 Ok message after checkout the session" in {

    }

    "get 404 NotFound message when trying to checkout a session that does not exist" in {

    }

    "get 200 Ok message after cancel the session" in {

    }

    "get 404 NotFound message when trying to cancel a session that does not exist" in {

    }
  }
}
