package com.datahack.akka.http.model.daos

import com.datahack.akka.http.model.dtos.{Product, User}
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

class ProductDao {
  val db = Database.forConfig("h2mem1")
  lazy val products: TableQuery[ProductTable] = TableQuery[ProductTable]

  def getAll: Future[Seq[Product]] = db.run(products.result)

  def getById(id: Long): Future[Option[Product]] =
    db.run(products.filter(_.id === id).result.headOption)

  def insert(product: Product): Future[Long] =
    db.run(products returning products.map(_.id) += product)

  def update(product: Product): Future[Int] = {
    db.run(products.filter(_.id === product.id).update(product))
  }

  def delete(id: Long): Future[Int] = db.run(products.filter(_.id === id).delete)
}

class ProductTable(tag: Tag) extends Table[Product](tag, "product") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def producer: Rep[String] = column[String]("producer")

  def name: Rep[String] = column[String]("name")

  def description: Rep[String] = column[String]("description")

  def price: Rep[Float] = column[Float]("price")

  def units: Rep[Float] = column[Float]("units")

  def * : ProvenShape[Product] = (id.?, producer, name, description, price, units) <>
    ((Product.apply _).tupled, Product.unapply)
}
