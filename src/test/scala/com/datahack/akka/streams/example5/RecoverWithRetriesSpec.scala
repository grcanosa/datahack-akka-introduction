package com.datahack.akka.streams.example5

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

// TODO: extender los traits necesario par los test
class RecoverWithRetriesSpec
  extends TestKit(ActorSystem("RecoverWithRetriesSpec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  // TODO: crear el materializdor para los test
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  "Recover with retries" should {

    "teach you how to use method recoverWithRetries to handle errors in streams" in {

      // TODO: obtenemos una referencia al singleton del RecoverWithRetries
      val recoverWithRetries = RecoverWithRetries

      // TODO: materializamos el source de RecoverFlow con un TestSink
      // Probamos que llegan todos los mensajes que esperamos y en su orden.
      // Probamos que stream se completa
      val probe: TestSubscriber.Probe[String] = recoverWithRetries.source.runWith(TestSink.probe[String])
      probe.request(1).expectNext("0")
        .request(1).expectNext("1")
        .request(1).expectNext("2")
        .request(1).expectNext("3")
        .request(1).expectNext("4")
        .request(1).expectNext("five")
        .request(1).expectNext("six")
        .request(1).expectNext("seven")
        .request(1).expectNext("eight")
        .expectComplete()
    }
  }
}
