package com.datahack.akka.introduction.actors

import akka.actor.{Actor, ActorLogging}

import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}

object Teacher{
  case class AskAdvice(topic:String)
  case class Advice(text:String)
  case object IDoNotUnderstand
}

class Teacher extends Actor with ActorLogging{

  log.debug(s"Creating teacher: ${self.path}")

  var advices:Map[String,String] = Map[String,String] (
    "History" -> "Moderation is for cowards",
    "Maths" -> "Anything worth doing is worth overdoing",
    "Geography" -> "Anything worth doing is worth overdoing",
    "Physics" -> "The trouble is you think you have time",
    "Literature" -> "You never gonna know if you never even try"
  )



  override def receive: Receive = {
    case AskAdvice(topic) =>
      val response = advices.get(topic).map(advice => Advice(advice)).getOrElse(IDoNotUnderstand)
      sender ! response
  }
}
