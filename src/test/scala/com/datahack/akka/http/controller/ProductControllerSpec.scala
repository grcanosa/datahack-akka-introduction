package com.datahack.akka.http.controller

import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestActorRef
import com.datahack.akka.http.controller.actors.ProductControllerActor
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.{JsonSupport, Product}
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import spray.json._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ProductControllerSpec
  extends WordSpec
    with Matchers {


  "Product Controller" should {

    "get all products offered in the application" in {

    }

    "get specific product offered by it id" in {

    }

    "get not found status code when searching a product that is not offered" in {

    }

    "add a new prdouct" in {

    }

    "update the product data" in {

    }

    "get Not Found status code when trying to update a product that is not offered" in {

    }

    "delete an offered product" in {

    }

    "get Not Found status code when trying to delete a product not offered" in {

    }
  }
}
