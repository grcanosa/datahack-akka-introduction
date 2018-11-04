package com.datahack.akka.streams.example1.actors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.{TestActorRef, TestKit}
import com.datahack.akka.streams.example1.actors.QueueActor.SendAdvice
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

// TODO: extendemos de los traits necesarios para los test
class QueueActorSpec {

  // TODO: creamos el materializador para los test
  implicit val materializer: ActorMaterializer = ???

  /*
  "Exercise Streams 1" should {

    "teach you how to use an actor to inject messages into a stream" in {
      // TODO: crear un flujo materializado con un source queue de TextMessate y un Test Sink de TextMesajes y
      // nos quedamos con los dos
      val (queue, probe) = ???

      // TODO: crear e iniciar el QueueActor con TestActorRef y pararle el source por parámeto
      val queueActor: TestActorRef[QueueActor] = ???

      // TODO: decirle al actor que envíe un nuevo consejo al stream

      // TODO: Probamos que el consejo se recive por el stream

    }
  }
  */

}