package com.datahack.akka.streams.example6

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.TestKit
import com.datahack.akka.streams.example6.RecoverWithSupervisor
import com.datahack.akka.streams.example6.RecoverWithSupervisor.source
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class RecoverWithSupervisorSpec {

  implicit val materializer: ActorMaterializer = ???

  /*
  "Recover with supervisor" should {

    "teach you how to recover from errors in a stream using a Recover decider" in {

      // TODO: obtenemos una referencia al singleton del RecoverWithRetries

      // TODO: materializamos el source del recoverWithSupervisor con un sink fold que sume los datos
      // probamos que el resultado es el esperado

    }
  }
  */
}
