package com.datahack.akka.streams.example3

import akka.actor.{Actor, ActorLogging}
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

/*
 * Es una ampliación del ejemplo anterior para aprender como parar un stream antes de que se haya terminado de consumir
 * sus mensajes.
 */

// TODO: Creamos un actor que recive de forma implícita el materializador
class PrintMoreNumbers(implicit materializer: ActorMaterializer) extends Actor with ActorLogging {

  // TODO: cremos un execution context  a partir del contexto
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  // TODO: creamos un source de enteros de 1 a 10 que posteriormente transformamos a strings y despues materializamos
  // imprimiendo sus valores por pantalla.
  // Vamos a añadir un materializador KillSwitch que nos permitira controlar el flujo y terminarlo
  // Por último vamos a hñadir un sink que recorra los elementos y los imprima por pantalla
  // Nos vamos a quedar con el falor final materializado en un Done
  private val (killSwitch, done) =
    Source.tick(0 seconds, 1 second, 1)
      .scan(0)(_ + _)
      .map(_.toString)
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(Sink.foreach(println))(Keep.both)
      .run()

  // TODO: utilizamos el done para que cuando termine el fujo podamos mandar al propio acotor el mensjar ede "done"
  done.map(_ => self ! "done")

  // TODO: Implementamos el comportamiento del ator
  override def receive: Receive = {
    // TODO: cuando recive el mensje "stop", logueamos su recepción y utilizamos el killswitch para termiar el stream
    case "stop" =>
      log.info("Stopping")
      killSwitch.shutdown()
    // TODO: cuando recivimos el mensaje "done" logeamos su recepción y utilizamos el contexto para parar el actor
    case "done" =>
      log.info("Done")
      context.stop(self)
  }
}
