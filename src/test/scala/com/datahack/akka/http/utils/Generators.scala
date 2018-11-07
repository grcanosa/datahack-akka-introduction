package com.datahack.akka.http.utils

import com.datahack.akka.http.model.dtos.User
import com.datahack.akka.http.model.dtos.Product
import org.scalacheck.Gen

trait Generators {

  val genUserName: Gen[String] = for {
    id <- Gen.chooseNum(1, 100)
  } yield { s"user$id"}

  val genPassword: Gen[String] = for {
    length <- Gen.chooseNum(4, 7)
    password <- Gen.uuid
  } yield password.toString.substring(0, length)

  val genUser: Gen[User] = for {
    name <- genUserName
    password <- genPassword
  } yield {
    User(
      id = None,
      name = name,
      email = s"$name@mail.com",
      password = password
    )
  }

  def genProductName: Gen[String] = for {
    id <- Gen.chooseNum(1, 100)
  } yield s"Product $id"

  def genProducerName: Gen[String] = for {
    id <- Gen.chooseNum(1, 100)
  } yield s"Producer $id"

  def genProduct: Gen[Product] = for {
    name <- genProductName
    producer <- genProducerName
    price <- Gen.chooseNum(0.0F, 50.0F)
    units <- Gen.chooseNum(0.0F, 100.0F)
  } yield {
    Product(
      id = None,
      producer = producer,
      name = name,
      description = s"Description of $name",
      price = price,
      units = units
    )
  }
}
