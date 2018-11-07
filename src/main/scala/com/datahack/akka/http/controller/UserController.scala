package com.datahack.akka.http.controller

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import akka.pattern.ask
import com.datahack.akka.http.controller.actors.UserControllerActor._
import com.datahack.akka.http.model.dtos.{JsonSupport, User}
import com.datahack.akka.http.service.UserService._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class UserController{

  implicit val timeout: Timeout = Timeout(60 seconds)

  val routes: Route = ???

  def getAllUsers: server.Route = ???

  def getUser: server.Route = ???

  def insertUser: server.Route = ???

  def updateUser: server.Route = ???

  def deleteUser: server.Route = ???

}

