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
  val config = ???
  val host = ???
  val port = ???

  // TODO: creamos de forma implícita el actor system
  implicit lazy val system = ???

  // TODO: creamos de forma implícita el execution context para gestionar el bind del actor Http
  implicit lazy val executionContext: ExecutionContextExecutor = ???

  // TODO: creamos de forma implícita el materializador que necesita el servidor Http
  implicit lazy val materializer: ActorMaterializer = ???

  // TODO: creamos el objeto controllador del websocket
  lazy val webSocketController = ???

  // TODO: scheduleamos el mensaje que vamos a enviar al actor que gestiona el source

  // TODO: iniciamos el servidor HTTP
  val routes = ???

  // TODO: añadimos el shutdown hook para terminar el sistema de actores cuando termine el programa

}
