package com.datahack.akka.http.controller

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestActorRef
import com.datahack.akka.http.controller.actors.UserControllerActor
import com.datahack.akka.http.model.daos.UserDao
import com.datahack.akka.http.model.dtos.{JsonSupport, User}
import com.datahack.akka.http.service.UserService
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class UserControllerSpec
  extends WordSpec
    with Matchers {

  "User Controller" should {

    "get all users signed up in the application" in {

    }

    "get specific signed up user by it id" in {

    }

    "get not found status code when shearching a user that is not signed up" in {

    }

    "sign up a new user" in {

    }

    "update the user data" in {

    }

    "get Not Found status code when trying to update a user that is not signed up" in {

    }

    "delete a signed user" in {

    }

    "get Not Found status code when trying to delete a user not signed up into the app" in {

    }
  }

}
