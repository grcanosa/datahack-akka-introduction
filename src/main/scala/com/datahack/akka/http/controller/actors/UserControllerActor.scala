package com.datahack.akka.http.controller.actors

import akka.actor.Actor
import akka.pattern.pipe
import com.datahack.akka.http.controller.actors.UserControllerActor._
import com.datahack.akka.http.model.dtos.User
import com.datahack.akka.http.service.UserService

import scala.concurrent.ExecutionContextExecutor

object UserControllerActor {
  case object GetAllUsers
  case class SearchUser(id: Long)
  case class CreateUser(user: User)
  case class UpdateUser(user: User)
  case class DeleteUser(id: Long)
}

class UserControllerActor extends Actor {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {

  }
}
