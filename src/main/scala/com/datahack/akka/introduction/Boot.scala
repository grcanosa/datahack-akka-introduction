package com.datahack.akka.introduction

import akka.actor.{ActorRef, ActorSystem, Props}

import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.{Student, Teacher}
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Boot extends App {


  val actorSystem = ActorSystem("UniversityMessagesSystem")

  implicit val executionContext:ExecutionContextExecutor = actorSystem.dispatcher

  val teacherActorRef:ActorRef = actorSystem.actorOf(Props[Teacher],"teacher1")
  //el path de este teacher sería /user/teacher1 (porque le hemos puesto nombre)

  val studentActorRef:ActorRef = actorSystem.actorOf(Props(classOf[Student],teacherActorRef),"student1")
  //path sería /user/student1


  actorSystem.scheduler
    .schedule(5.seconds      //a 5 segundos de esta llamada
      , 3 seconds   //y cada 5 segundos
      , studentActorRef       //manda al studentActorRef
      , PerformAnAdviceRequest)  //el mensaje este.

  //Por defecto Akka busca un application.conf en cualquier sitio.SI hay varios los va apilando cogiendo el ultimo valor.


  //Intercepta la señal de terminar apliación y salir de forma ordenada.
  sys.addShutdownHook(actorSystem.terminate())





}
