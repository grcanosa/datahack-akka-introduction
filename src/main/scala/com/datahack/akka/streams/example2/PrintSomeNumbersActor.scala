package com.datahack.akka.streams.example2

import akka.actor.{Actor, ActorLogging}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.ExecutionContextExecutor

/*
 * Vamos a aprender a encapsular un stream dentro de un actor.
 */


// TODO: Creamos un actor que recive de forma implícita el materializador
class PrintSomeNumbersActor {

  // TODO: cremos un execution context  a partir del contexto
  private implicit val executionContext: ExecutionContextExecutor = ???

  // TODO: creamos un source de enteros de 1 a 10 que posteriormente transformamos a strings y despues materializamos
  // imprimiendo sus valores por pantalla. Una vez imprimidos los valores enviamos un mensaje "done" al propio actor
  // para indicar que ha terminado de consumir todos los eventos del stream.


  // TODO: implementar el comportamiento del actor para que cuando reciva el mensaje "done" imprima por el log de info
  // que ha terminado de prcesar el stream y despues el actor se para a sí mismo.

}