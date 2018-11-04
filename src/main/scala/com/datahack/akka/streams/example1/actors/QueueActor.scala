package com.datahack.akka.streams.example1.actors

import akka.actor.Actor
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.scaladsl.SourceQueueWithComplete
import com.datahack.akka.streams.example1.actors.QueueActor.SendAdvice

/*
 * Vamos ha aprender como generar eventos en un stream desde un actor
 */

// Encapsulamos los mensajes que va ha manejar el actor
object QueueActor {
  case class SendAdvice(advice: String) // envía el mensaje que el actor va ha encolar en el stream
}

// TODO: El actor recive el source materializada. En este caso una cola que admite mensajes de tipo TextMessage
class QueueActor(queue: SourceQueueWithComplete[TextMessage]) extends Actor { // TODO: es necesario que la clase sea un actor

  override def receive: Receive = {
    // TODO: Cuando reciva el mensaje SendAdvice, encola el mensaje en el souce que recive por parámetro
    case SendAdvice(advice) =>
      queue.offer(TextMessage(advice))
  }
}
