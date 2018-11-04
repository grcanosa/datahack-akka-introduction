package com.datahack.akka.streams.example7

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, RestartSource, Sink, Source}
import com.datahack.akka.streams.example5.RecoverWithRetries.{runFlow, system}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

object RecoverWithBackoff {

  //create the actor system
  implicit val system: ActorSystem = ActorSystem("RecoverWithBackoff")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  case class Unmarshal(b: Any) {
    def to[T]: Future[T] = Promise[T]().future
  }

  case class ServerSentEvent()

  val restartSource = RestartSource.withBackoff(
    minBackoff = 3.seconds,
    maxBackoff = 30.seconds,
    randomFactor = 0.2, // adds 20% "noise" to vary the intervals slightly
    maxRestarts = 20 // limits the amount of restarts to 20
  ) { () ⇒
    // Create a source from a future of a source
    Source.fromFutureSource {
      // Make a single request with akka-http
      Http().singleRequest(HttpRequest(
        uri = "http://example.com/eventstream"
      ))
        // Unmarshall it as a source of server sent events
        .flatMap(Unmarshal(_).to[Source[ServerSentEvent, NotUsed]])
    }
  }

  def main(args: Array[String]) = {
    //#with-kill-switch
    val killSwitch = restartSource
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(Sink.foreach(event ⇒ println(s"Got event: $event")))(Keep.left)
      .run()

    killSwitch.shutdown()
  }

}
