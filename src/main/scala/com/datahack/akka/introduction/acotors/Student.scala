package com.datahack.akka.introduction.acotors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.datahack.akka.introduction.acotors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.acotors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import org.scalacheck.Gen

object Student {

  case object PerformAnAdviceRequest
}

class Student(teacher: ActorRef) extends Actor with ActorLogging{

  log.debug(s"${self.path} actor created")

  val genTopics: Gen[String] = Gen.oneOf(
    "History", "Maths", "Geography", "Physics", "Literature", "Biology")

  override def receive: Receive = {
    case PerformAnAdviceRequest =>
      val topic = genTopics.sample.get
      teacher ! AskAdvice(topic)

    case Advice(text) =>
      log.info(s"The requested advice: $text.")

    case IDoNotUnderstand =>
      log.error("Ups I do not know what happens.")
  }
}
