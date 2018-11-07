package com.datahack.akka.streams.example5

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

// TODO: extender los traits necesario par los test
class RecoverWithRetriesSpec {

  // TODO: crear el materializdor para los test
  implicit val materializer: ActorMaterializer = ???

  /*
  "Recover with retries" should {

    "teach you how to use method recoverWithRetries to handle errors in streams" in {

      // TODO: obtenemos una referencia al singleton del RecoverWithRetries

      // TODO: materializamos el source de RecoverFlow con un TestSink
      // Probamos que llegan todos los mensajes que esperamos y en su orden.
      // Probamos que stream se completa

    }
  }
  */
}
