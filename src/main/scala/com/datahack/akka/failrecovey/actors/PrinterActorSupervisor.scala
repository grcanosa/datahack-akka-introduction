package com.datahack.akka.failrecovey.actors

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import com.datahack.akka.failrecovey.errors.{RestartMeException, ResumeMeException, StopMeException}

class PrinterActorSupervisor extends Actor with ActorLogging {

  override def preStart() = log.info("The Supervisor is ready to supervise")
  override def postStop() = log.info("Bye Bye from the Supervisor")

  override def supervisorStrategy = OneForOneStrategy() {
    case _: RestartMeException => Restart
    case _: ResumeMeException => Resume
    case _: StopMeException => Stop
  }

  val printer = context.actorOf(Props[PrinterActor], "printer-actor")

  override def receive: Receive = {
    case msg => printer forward msg
  }

}
