package com.datahack.akka.failrecovey

import akka.actor.{ActorSystem, Props}
import com.datahack.akka.failrecovey.actors.PrinterActor.Message
import com.datahack.akka.failrecovey.actors.PrinterActorSupervisor
import com.datahack.akka.introduction.Boot.actorSystem

object Boot extends App {

  implicit val system = ActorSystem("PrinterService")
  val printerSupervisor = system.actorOf(Props[PrinterActorSupervisor], "printer-supervisor")
  // "The Supervisor is ready to supervise"
  // "Yo, I am alive!"

  printerSupervisor ! Message("...please, print me...")
  // ...please, print me...
  printerSupervisor ! Message("...another message to print, nothing should happen...")
  // ...another message to print, nothing should happen...

  printerSupervisor ! Message("...why don't you restart?!")
  //  ...why don't you restart?!
  //  Yo, I am restarting...
  //  Goodbye world!
  //  ...restart completed!
  //  Yo, I am alive!

  // From the logs:
  // ERROR [OneForOneStrategy]: RESTART
  // com.danielasfregola.RestartMeException: RESTART
  //    at com.danielasfregola.PrinterActor$$anonfun$receive$1.applyOrElse(PrinterActor.scala:24) ~[classes/:na]

  printerSupervisor ! Message("...fell free to resume!")
  // ...fell free to resume!

  // From the logs:
  // WARN  [OneForOneStrategy]: RESUME

  printerSupervisor ! Message("...you can STOP now!")
  // ...you can STOP now!
  // Goodbye world!

  // From the logs:
  // ERROR [OneForOneStrategy]: STOP
  // com.danielasfregola.StopMeException: STOP
  //    at com.danielasfregola.PrinterActor$$anonfun$receive$1.applyOrElse(PrinterActor.scala:28) ~[classes/:na]

  printerSupervisor ! Message("...this is going to be our little secret...")
  // ...this is going to be our little secret...
  // Goodbye world!
  // Bye Bye from the Supervisor

  // From the logs:
  // ERROR [LocalActorRefProvider(akka://printer-service)]: guardian failed, shutting down system
  // java.lang.Throwable: null
  //    at com.danielasfregola.PrinterActor$$anonfun$receive$1.applyOrElse(PrinterActor.scala:30) ~[classes/:na]

  // Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(actorSystem.terminate())
}
