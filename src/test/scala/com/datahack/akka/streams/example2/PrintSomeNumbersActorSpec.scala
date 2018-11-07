package com.datahack.akka.streams.example2

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.testkit.{EventFilter, TestKit}
import com.datahack.akka.streams.example2.PrintSomeNumbersActor
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

// TODO: extendemos de los traits necesarios para los test
// en este caso vamos a indicarle al acotr sustem que utilice el TestEvenListener para logear los test
// y poder probar los actores
class PrintSomeNumbersActorSpec {

  // TODO: crear el materializador para los test
  implicit val materializer: ActorMaterializer = ???

  /*
  "Print Some Numbers Actor" should {

    "teach you how to encapsulate a flow into an actor" in {
      // TODO: crear un actor PrintSomeNumbers y probar que llega el mensje "Done"

    }
  }
  */
}