package com.datahack.akka.streams.example3

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.testkit.{EventFilter, TestKit}
import com.datahack.akka.streams.example3.PrintMoreNumbers
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContextExecutor

// TODO: extendemos de los traits necesarios para los test
// en este caso vamos a indicarle al acotr sustem que utilice el TestEvenListener para logear los test
// y poder probar los actores
// Además vams a indicarle también que sobrescriba el valor filter-leeway para aumentar el timeout de los test
class PrintMoreNumbersSpec {

  // TODO: crear el materializador para los test
  implicit val materializer: ActorMaterializer = ???

  // TODO: crear el execution context para los test
  implicit val executionContext: ExecutionContextExecutor = ???

  /*
  "Print More Numbers Actor" should {

    "teach you how to encapsulate a flow into an actor" in {
      // TODO: crear el actor PrintMoreNumbers

      // TODO: probar que se loguea el mensaje "Stopping" cuando el actor recive el mensaje "stop"

    }
  }
  */
}