package com.datahack.akka.streams.example5

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}

/*
 * Vamos a ver otra alternativa a gestionar errores en el flujo mejorando el caso anterior utilizando
 * RecoverWithRetries:
 *
 * recoverWithRetries allows you to put a new upstream in place of the failed one,
 * recovering stream failures up to a specified maximum number of times.
 *
 * Deciding which exceptions should be recovered is done through a PartialFunction.
 * If an exception does not have a matching case the stream is failed.
 */
object RecoverWithRetries {

  // TODO: creamos de forma implícita el actor system
  implicit val system: ActorSystem = ???

  // TODO: creamos de forma implícita el execution context para gestionar el bind del actor Http
  implicit val executionContext: ExecutionContext = ???

  // TODO: creamos de forma implícita el materializador que necesitamos para materializar el stream
  implicit val materializer: ActorMaterializer = ???

  // Esta lista es la que vamos a añadir al stream en caso de que haya algún fallo en el stream
  val planB = Source(List("five", "six", "seven", "eight"))

  // TODO: creamos un source que emita valores enteros del 0 al 6
  // si el valor es menor de 5 lo pasamos a string
  // sino lanzamos una RuntimeException
  // utilizamos el método recoverWithRetries para capturar la excepción y añadir al stream la lista del plan b

  // TODO: creamos el método runFlow que materializa el source y por cada elemento del stream lo pinta por pantalla
  def runFlow: Future[Done] = ???

  // TODO: creamos el método main que llama al método runFlow y cuando se completa el flujo terminamos el actor system
  // vamos ha sobrescribir este método en vez de utilizar el trait App para poder testear el flujo
  def main(args: Array[String]) = ???

}
