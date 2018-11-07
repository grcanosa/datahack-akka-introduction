package com.datahack.akka.http.model.daos

import com.datahack.akka.http.model.dtos.User
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class UserDao {
  val db = Database.forConfig("h2mem1")
  lazy val users: TableQuery[UserTable] = TableQuery[UserTable]

  def getAll: Future[Seq[User]] = db.run(users.result)

  def getById(id: Long): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)

  def insert(user: User): Future[Long] =
    db.run(users returning users.map(_.id) += user)

  def update(user: User): Future[Int] = {
    db.run(users.filter(_.id === user.id).update(user))
  }

  def delete(id: Long): Future[Int] = db.run(users.filter(_.id === id).delete)

}

class UserTable(tag: Tag) extends Table[User](tag, "user") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("name")

  def email: Rep[String] = column[String]("email")

  def password: Rep[String] = column[String]("password")

  def * : ProvenShape[User] = (id.?, name, email, password) <> ((User.apply _).tupled, User.unapply)
}
