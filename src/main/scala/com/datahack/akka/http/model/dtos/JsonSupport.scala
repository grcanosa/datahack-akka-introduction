package com.datahack.akka.http.model.dtos

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, PrettyPrinter}

/*
 * Trait con los formaters del spray para el marshalling and unmarshalling de los DTOS
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val printer = PrettyPrinter

  implicit val userFormat = jsonFormat4(User)

  implicit val productFormat = jsonFormat6(Product)

  implicit val orderFormat = jsonFormat3(Order)

}
