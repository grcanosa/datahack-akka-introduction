package com.datahack.akka.persistence

import akka.actor.{ActorSystem, Props}
import com.datahack.akka.persistence.actors.Basket
import com.datahack.akka.persistence.actors.Basket.{Boom, Cmd, Print, Snap}

object Boot extends App {

  val actorSystem = ActorSystem("PersistenceSystem")

  val persistentActor = actorSystem.actorOf(Props[Basket],"Basket")

  persistentActor ! Print

  persistentActor ! Cmd("foo")

  persistentActor ! Cmd("baz")

  persistentActor ! Boom

  persistentActor ! Cmd("bar")

  persistentActor ! Snap

  persistentActor ! Cmd("buzz")

  persistentActor ! Print

  sys.addShutdownHook(actorSystem.terminate())

  

}
