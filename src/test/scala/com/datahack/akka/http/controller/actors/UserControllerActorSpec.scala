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

class UserControllerActorSpec
  extends TestKit(ActorSystem("UserControllerActorSpec"))
    with WordSpecLike
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val users: Seq[User] = (1 to 5).map(i => genUser.sample.get.copy(id = Some(i)))
  lazy val userDao: UserDao = new UserDao
  lazy val userService: UserService = new UserService(userDao)
  lazy val userControllerActor: TestActorRef[UserControllerActor] =
    TestActorRef[UserControllerActor](new UserControllerActor(userService))

  implicit val executionContext = system.dispatcher

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(users.toList, schemaName)), 5 seconds)
  }

  "User Controller Actor" should {

    "get all stored users" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      userControllerActor ! GetAllUsers

      sender.expectMsg(AllUsers(users))
    }

    "search a user by id" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      userControllerActor ! SearchUser(users.head.id.get)

      sender.expectMsg(FoundUser(users.head))
    }

    "get UserNotFound message if the user that we are searching by id it does not exist into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      userControllerActor ! SearchUser(users.length + 10)

      sender.expectMsg(UserNotFound)
    }

    "insert a user into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val userToInsert: User = genUser.sample.get.copy(id = None)

      userControllerActor ! CreateUser(userToInsert)

      val userStored: StoredUser = sender.expectMsgType[StoredUser]

      val Some(userFound) = Await.result(SqlTestUtils.findEntity(userStored.user.get, schemaName), 5 seconds)

      userStored.user.get shouldBe userFound
    }

    "update a user stored into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val userToUpdate: User = users.last.copy(name = "TheNewName")

      userControllerActor ! UpdateUser(userToUpdate)

      sender.expectMsg(UpdatedUser(userToUpdate))

      val Some(userUpdated) = Await.result(SqlTestUtils.findEntity(userToUpdate, schemaName), 5 seconds)
      userUpdated shouldBe userToUpdate
    }

    "get UserNotFound message when trying to update a user that not exit into database" in  {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val userToUpdate: User = users.last.copy(id = Some(users.length + 20), name = "TheNewName")

      userControllerActor ! UpdateUser(userToUpdate)

      sender.expectMsg(UserNotFound)
    }

    "delete a user stored into database" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      val userToDelete = users.head

      userControllerActor ! DeleteUser(userToDelete.id.get)

      sender.expectMsg(UserDeleted)

      val userStored = Await.result(SqlTestUtils.findEntity(userToDelete, schemaName), 5 seconds)
      userStored shouldBe None
    }

    "get UserNotFound message whe trying to delete a user that not exits" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      userControllerActor ! DeleteUser(users.length + 20)

      sender.expectMsg(UserNotFound)

    }

  }

  override protected def afterAll(): Unit = {
    Await.result(SqlTestUtils.dropDatabase(schemaName), 5 seconds)
  }
}
