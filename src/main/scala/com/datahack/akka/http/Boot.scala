package com.datahack.akka.http

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import com.datahack.akka.http.controller.{ProductController, SessionController, UserController}
import com.datahack.akka.http.controller.actors.{ProductControllerActor, SessionControllerActor, UserControllerActor}
import com.datahack.akka.http.model.daos.{ProductDao, UserDao}
import com.datahack.akka.http.service.actors.Inventory
import com.datahack.akka.http.service.actors.Inventory.InitInventory
import com.datahack.akka.http.service.{ProductService, UserService}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor

object Boot extends App with Directives {

  // This configs are in the application.conf file
  val config = ConfigFactory.load()
  val host = config.getString("http.host") // Gets the host and a port from the configuration
  val port = config.getInt("http.port")

  implicit lazy val system: ActorSystem = ActorSystem("AkkaHttpExample")  // ActorMaterializer requires an implicit ActorSystem
  implicit lazy val ec: ExecutionContextExecutor = system.dispatcher  // bindingFuture.map requires an implicit ExecutionContext

  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()  // bindAndHandle requires an implicit materializer

  // User controller
  lazy val userDao = new UserDao
  lazy val userService = new UserService(userDao)
  lazy val userActor = system.actorOf(Props(classOf[UserControllerActor], userService), "UserController")
  lazy val userController = new UserController(userActor)

  // Product controller
  lazy val productDao = new ProductDao
  lazy val productService = new ProductService(productDao)
  lazy val productActor = system.actorOf(Props(classOf[ProductControllerActor], productService), "ProductController")
  lazy val productController = new ProductController(productActor)

  // Session controller
  lazy val inventoryActor = system.actorOf(Props(classOf[Inventory], productService), "Inventory")
  lazy val sessionControllerActor = system.actorOf(Props(classOf[SessionControllerActor], inventoryActor), "SessionController")
  lazy val sessionController = new SessionController(sessionControllerActor)

  inventoryActor ! InitInventory

  // Start HTTP server
  // TODO: Add users controller routes
  // TODO: Add products controller routes
  // TODO: Add session controller routes
  val routes = userController.routes ~ productController.routes ~ sessionController.routes
  Http().bindAndHandle(routes, host, port)

  // Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.terminate())
}
