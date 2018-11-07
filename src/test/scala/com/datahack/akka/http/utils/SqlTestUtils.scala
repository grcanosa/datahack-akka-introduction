package com.datahack.akka.http.utils

import com.datahack.akka.http.model.daos.{ProductTable, UserTable}
import com.datahack.akka.http.model.dtos.User
import com.datahack.akka.http.model.dtos.Product
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SqlTestUtils {

  val db = Database.forConfig("h2mem1")

  case class Tables(schemaName: String) {

    val users = TableQuery[UserTable]
    val products = TableQuery[ProductTable]

    val tableQueriesList = List(
      users,
      products
    )

    val queryTables = Map(
      "users" -> users,
      "products" -> products
    )

  }

  def createSchema(schemaName: Option[String] = None) : Future[String] = {
    val schema = schemaName.getOrElse(s"db_${System.currentTimeMillis().toString}")
    db.run {
      DBIOAction.seq(sqlu"CREATE SCHEMA #$schema")
    }.map(_ => schema)
  }

  def createTables(tables: List[DBIOAction[_, NoStream, Effect.Schema]]): Future[Unit] = {
    db.run {
      DBIOAction.seq(tables:_*)
    }
  }

  def populateSchema(schema: String): Future[String] = {
    val tables: Tables = Tables(schema)
    createTables(tables.tableQueriesList.map(_.schema.create)).map(_ => schema)
  }

  def initDatabase(schemaName: Option[String] = None): Future[String] = {
    val schema = schemaName.getOrElse(s"db_${System.currentTimeMillis().toString}")

    for {
      schemaCreated <- createSchema(Some(schema))
      schemaPopulated <- populateSchema(schemaCreated)
    } yield schemaPopulated
  }

  def executeQueries(queries: List[String]): Future[Unit] = {
    db.run {
      DBIOAction.seq(queries.map(query => sqlu"#$query"):_*)
    }
  }

  def dropDatabase(schemaName: String): Future[Unit] = {
    val tables = Tables(schemaName)
    db.run {
      DBIOAction.seq(
        tables.users.schema.drop,
        tables.products.schema.drop,
        sqlu"DROP SCHEMA #$schemaName CASCADE"
      )
    }
  }

  def dropDatabaseOnCascade(schemaName: String) = {
    db.run {
      DBIOAction.seq(sqlu"DROP SCHEMA #$schemaName CASCADE")
    }
  }

  def insertList(rows: List[Any], schemaName: String): List[Future[Int]] =
    rows.map(insert(_, schemaName))

  //scalastyle:off
  def insert(row: Any, schemaName: String): Future[Int] = {
    val tables = Tables(schemaName)
    row match {
      case user: User => db.run(tables.users += user)
      case product: Product => db.run(tables.products += product)
      case _ => throw new Exception(s"Unknown Entity ${row.getClass.getName}")
    }
  }

  def findEntity(entity: Any, schemaName: String): Future[Any] = {
    val tables = Tables(schemaName)
    entity match {
      case user: User =>
        db.run(tables.users.filter(_.id === user.id).result)
          .map(_.headOption)
      case product: Product =>
        db.run(tables.products.filter(_.id === product.id).result)
          .map(_.headOption)
      case _ => throw new Exception(s"Unknown Entity ${entity.getClass.getName}")
    }
  }
  //scalastyle:on
}
