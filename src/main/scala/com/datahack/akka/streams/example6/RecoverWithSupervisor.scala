package com.datahack.akka.streams.example6

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.ExecutionContext

/*
 * Vamos a aprender como utilizar un supervisor para gestionar los fallos en un stream
 */
object RecoverWithSupervisor {

  // TODO: creamos de forma implícita el actor system
  implicit val system: ActorSystem = ???

  // TODO: creamos de forma implícita el execution context para gestionar el bind del actor Http
  implicit val executionContext: ExecutionContext = ???

  // TODO: creamos de forma implícita el materializador que necesitamos para materializar el stream
  implicit val materializer: ActorMaterializer = ???

  // TODO: creamos el suervisor. Cuando capture la excepción ArithmeticException hará un resume del actor y un stop
  // en cualquier otro caso
  val decider: Supervision.Decider = ???

  // TODO: creamos un flow que filtrará los valores que al dividirlos de 100 sean menores que 50
  // añadirmos el supervisor al flujo
  val flow: Flow[Int, Int, NotUsed] = ???

  // creamos un source de enteros de 0 a 5 y lo añadimos al flujo anterior
  val source: Source[Int, NotUsed] = ???

  // TODO: sobrescribimos el método main
  def main(args: Array[String]) = {

    // TODO: materializamos el source creado y lo matreializamos con un sink que sume todos los valores
    // the elements causing division by zero will be dropped
    // result here will be a Future completed with Success(150)

    // TODO: cuando se complete el flujo imprimos por pantalla la suma y terminamos el actor system

  }

}
