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
  val config = ???
  val host = ???
  val port = ???

  implicit lazy val system: ActorSystem = ???  // ActorMaterializer requires an implicit ActorSystem
  implicit lazy val ec: ExecutionContextExecutor = ??? // bindingFuture.map requires an implicit ExecutionContext

  implicit lazy val materializer: ActorMaterializer = ???  // bindAndHandle requires an implicit materializer

  // User controller
  lazy val userDao = new UserDao
  lazy val userService = new UserService(userDao)
  lazy val userActor = ???
  lazy val userController = ???

  // Product controller
  lazy val productDao = new ProductDao
  lazy val productService = new ProductService(productDao)
  lazy val productActor = ???
  lazy val productController = ???

  // Session controller
  lazy val inventoryActor = ???
  lazy val sessionControllerActor = ???
  lazy val sessionController = ???

  // TODO: Initialize inventory
  //inventoryActor ! InitInventory

  // Start HTTP server
  // TODO: Add users controller routes
  // TODO: Add products controller routes
  // TODO: Add session controller routes
  val routes = ???
  // TODO: bind Http actor to host an port with controller routes

  // Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(system.terminate())
}
