package com.datahack.akka.streams.example1.controller

import akka.{Done, NotUsed}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, Sink, Source, SourceQueueWithComplete}
import com.datahack.akka.streams.example1.actors.QueueActor

import scala.concurrent.{ExecutionContext, Future}

/*
 * Vamos a aprender como materializar una stream utilizando un websocet como source de los mensajes de entrada
 * y como sink de un stream controlado por un actor
 */

// TODO: la clase recivirá de forma implícita una referencia al actor syste, un materializador y un contexto de ejecución
class WebSocket() {

  // TODO: Generamos un grafo a partir de un source queue y lo materializamos en un BroadcastHub que nos permite
  // reutilizar el source para todos las peticiones recogidas por el web socket y nos quedamos tanto con el
  // ssurce como con el source materializado
  val graph: (SourceQueueWithComplete[Message], Source[Message, NotUsed]) = ???

  // TODO: Para los mesajes que vengan del cliente a través del web socket creamos un sink que los imprima por pantalla
  val printSink: Sink[Message, Future[Done]] = ???

  // TODO: creamos un actor de tipo QueueActor que se va a encargar de injectar datos al souce queue que hemos creado antes
  val queueActorRef: ActorRef = ???

  // TODO: utilizamos la DSL de akka http para crear una ruta que maneje el websocket
  val websocketRoute: Route = ???

      // TODO: utilizaremos un Flow from sink and source para añadir el sink y el grafo que hemos creado antes
      // el Future[Done] es el valor materializado del Sink
      // y se completa cuando el stream se completa

      // TODO: utilizamos la DLS de akka http para manejar el websocket con el flow que hemos creado

}
