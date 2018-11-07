package com.datahack.akka.http.controller.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.controller.actors.UserControllerActor._
import com.datahack.akka.http.model.daos.UserDao
import com.datahack.akka.http.model.dtos.User
import com.datahack.akka.http.service.UserService
import com.datahack.akka.http.service.UserService._
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class UserControllerActorSpec {

  /*
  "User Controller Actor" should {

    "get all stored users" in {

    }

    "search a user by id" in {

    }

    "get UserNotFound message if the user that we are searching by id it does not exist into database" in {

    }

    "insert a user into database" in {

    }

    "update a user stored into database" in {

    }

    "get UserNotFound message when trying to update a user that not exit into database" in  {

    }

    "delete a user stored into database" in {

    }

    "get UserNotFound message whe trying to delete a user that not exits" in {

    }

  }
  */
}
