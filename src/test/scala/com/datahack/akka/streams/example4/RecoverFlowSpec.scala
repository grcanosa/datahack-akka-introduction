package com.datahack.akka.streams.example4

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec, WordSpecLike}

// TODO: extender los traits necesario par los test
class RecoverFlowSpec {

  // TODO: crear el materializdor para los test
  implicit val materializer: ActorMaterializer = ???

  /*
  "Recover Flow" should {

    "teach you how to use recover method to handle stream errors" in {

      // TODO: obtenemos una referencia al singleton del RecoverFow

      // TODO: materializamos el source de RecoverFlow con un TestSink
      // Probamos que llegan todos los mensajes que esperamos y en su orden.
      // Probamos que stream se completa

    }
  }
  */
}
