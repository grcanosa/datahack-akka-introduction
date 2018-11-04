package com.datahack.akka.streams.example4

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec, WordSpecLike}

// TODO: extender los traits necesario par los test
class RecoverFlowSpec
  extends TestKit(ActorSystem("RecoverFlowSpec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  // TODO: crear el materializdor para los test
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  "Recover Flow" should {

    "teach you how to use recover method to handle stream errors" in {

      // TODO: obtenemos una referencia al singleton del RecoverFow
      val recoverFlow = RecoverFlow

      // TODO: materializamos el source de RecoverFlow con un TestSink
      // Probamos que llegan todos los mensajes que esperamos y en su orden.
      // Probamos que stream se completa
      val probe: TestSubscriber.Probe[String] = recoverFlow.source.runWith(TestSink.probe[String])
      probe.request(1).expectNext("0")
        .request(1).expectNext("1")
        .request(1).expectNext("2")
        .request(1).expectNext("3")
        .request(1).expectNext("4")
        .request(1).expectNext("stream truncated")
        .expectComplete()
    }
  }
}
