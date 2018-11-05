package com.datahack.akka.failrecovey.actors

import akka.actor.{Actor, ActorLogging}
import com.datahack.akka.failrecovey.actors.PrinterActor.Message
import com.datahack.akka.failrecovey.errors.{RestartMeException, ResumeMeException, StopMeException}

object PrinterActor {

  case class Message(msg: String)

}

class PrinterActor extends Actor with ActorLogging {

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info("Yo, I am restarting...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    log.info("...restart completed!")
    super.postRestart(reason)
  }

  override def preStart() = log.info("Yo, I am alive!")
  override def postStop() = log.info("Goodbye world!")

  override def receive: Receive = {
    case Message(msg) if containsRestart(msg) =>
      log.debug(msg)
      throw new RestartMeException
    case Message(msg) if containsResume(msg) =>
      log.debug(msg)
      throw new ResumeMeException
    case Message(msg) if containsStop(msg) =>
      log.debug(msg)
      throw new StopMeException
    case Message(msg) if containsSecret(msg) =>
      log.debug(msg)
      throw new Throwable
    case Message(msg) => log.debug(msg)
  }

  private def containsRestart = containsWordCaseInsensitive("restart")_
  private def containsResume = containsWordCaseInsensitive("resume")_
  private def containsStop = containsWordCaseInsensitive("stop")_
  private def containsSecret = containsWordCaseInsensitive("secret")_

  private def containsWordCaseInsensitive(word: String)(msg: String) =  msg matches s".*(?i)$word.*"

}
