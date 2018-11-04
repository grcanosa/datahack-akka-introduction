package com.datahack.akka.streams.example1.controller

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.{ScalatestRouteTest, WSProbe}
import com.datahack.akka.streams.example1.actors.QueueActor.SendAdvice
import org.scalatest.{Matchers, WordSpec}

// TODO: extender de los traits necesarios para los test
class WebSocketSpec
    extends WordSpec
    with Matchers {

  // TODO: crear un controllador WebSocket
  val webSocketController = ???

  "Web socket controller" should {

    "listen to websocket and get the advices produced by the actor" in {
      // TODO: creamos un WebSocketClient de prueba
      val wsClient = ???

      // TODO: indicamos cual va ha ser el consejo que vamos a enviar por el socket
      val myAdvice: String = "this is my advice"

      // TODO: indicar al actor que controla el source queue qeue envíe el consejo anterior

      // TODO: utilizamos el test kit de akka http para probar el websocket
        // TODO: probamos que el socket ha hecho un upgrade de protocolo a websocket
        // TODO: probar que llega un mensaje al socket con el conejo que enviamos al actor SourceQueue

    }
  }

}
