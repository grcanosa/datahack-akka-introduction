package com.datahack.akka.http.model.dtos

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, PrettyPrinter, RootJsonFormat}

/*
 * Trait con los formaters del spray para el marshalling and unmarshalling de los DTOS
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val printer = PrettyPrinter

  implicit val userFormat: RootJsonFormat[User] = jsonFormat4(User)

  implicit val productFormat: RootJsonFormat[Product] = jsonFormat6(Product)

  implicit val orderFormat = jsonFormat3(Order)

}
