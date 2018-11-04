package com.datahack.akka.streams.example1

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datahack.akka.streams.example1.actors.QueueActor.SendAdvice
import com.datahack.akka.streams.example1.controller.WebSocket
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Boot extends App {

  // TODO: tomamos de configuración los valores del puerto y el host del servidor http que vamos a crear con akka http
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  // TODO: creamos de forma implícita el actor system
  implicit lazy val system = ActorSystem("PersistenceSystem")

  // TODO: creamos de forma implícita el execution context para gestionar el bind del actor Http
  implicit lazy val executionContext: ExecutionContextExecutor = system.dispatcher

  // TODO: creamos de forma implícita el materializador que necesita el servidor Http
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()

  // TODO: creamos el objeto controllador del websocket
  lazy val webSocketController = new WebSocket()

  // TODO: scheduleamos el mensaje que vamos a enviar al actor que gestiona el source
  system.scheduler.schedule(0 seconds, 5 seconds, webSocketController.queueActorRef, SendAdvice("my Advice"))

  // TODO: iniciamos el servidor HTTP
  val routes = webSocketController.websocketRoute
  Http().bindAndHandle(routes, host, port)

  // TODO: añadimos el shutdown hook para terminar el sistema de actores cuando termine el programa
  sys.addShutdownHook(system.terminate())

}
