package com.datahack.akka.introduction.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import org.scalacheck.Gen

import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}

object Student{

  case object PerformAnAdviceRequest
}

class Student(teacher:ActorRef) extends Actor with ActorLogging{

  log.debug(s"Creating student: ${self.path}")

  var genTopics:Gen[String] =
    Gen.oneOf("History","Maths","Geography","Physics","Literature","Biology")


  override def receive: Receive = {
    case PerformAnAdviceRequest =>
      val topic = genTopics.sample.get
      teacher ! AskAdvice(topic)
    case Advice(texto) => log.info(s"The requested advice: $texto")
    case IDoNotUnderstand => log.error("NPI!")

  }
}
