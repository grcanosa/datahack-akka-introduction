package com.datahack.akka.http.model.daos

import com.datahack.akka.http.model.dtos.User
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class UserDaoSpec
  extends WordSpec
    with Matchers
    with Generators
    with BeforeAndAfterAll {

  var schemaName: String = ""

  val users: Seq[User] = (1 to 5).map(i => genUser.sample.get.copy(id = Some(i)))
  lazy val userDao = new UserDao

  override protected def beforeAll(): Unit = {
    schemaName = Await.result(SqlTestUtils.initDatabase(), 5 seconds)
    Await.result(Future.sequence(SqlTestUtils.insertList(users.toList, schemaName)), 5 seconds)
  }

  "User DAO" should {

    "get all stored users" in {
      val result = Await.result(userDao.getAll, 5 seconds)
      result should have length users.length
    }

    "search a user by id" in {
      val Some(result) = Await.result(userDao.getById(users.head.id.get), 5 seconds)
      result shouldBe users.head
    }

    "get none if the user that we are searching by id it does not exist into database" in {
      val result = Await.result(userDao.getById(users.length + 10), 5 seconds)
      result shouldBe None
    }

    "insert a user into database" in {
      val userToInsert: User = genUser.sample.get.copy(id = None)
      val id = Await.result(userDao.insert(userToInsert), 5 seconds)

      val userToInsertWithId = userToInsert.copy(id = Some(id))
      val Some(userStored) = Await.result(SqlTestUtils.findEntity(userToInsertWithId, schemaName), 5 seconds)
      userStored shouldBe userToInsertWithId
    }

    "update a user stored into database" in {
      val userToUpdate: User = users.last.copy(name = "TheNewName")

      Await.result(userDao.update(userToUpdate), 5 seconds)

      val Some(userUpdated) = Await.result(SqlTestUtils.findEntity(userToUpdate, schemaName), 5 seconds)
      userUpdated shouldBe userToUpdate
    }

    "delete a user stored into database" in {
      val userToDelete = users.head
      val result = Await.result(userDao.delete(userToDelete.id.get), 5 seconds)
      result shouldBe 1
      val userStored = Await.result(SqlTestUtils.findEntity(userToDelete, schemaName), 5 seconds)
      userStored shouldBe None
    }
  }

  override protected def afterAll(): Unit = {
    Await.result(SqlTestUtils.dropDatabase(schemaName), 5 seconds)
  }
}
