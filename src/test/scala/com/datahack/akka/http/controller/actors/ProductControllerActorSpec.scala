package com.datahack.akka.http.controller.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.http.controller.actors.ProductControllerActor._
import com.datahack.akka.http.model.daos.ProductDao
import com.datahack.akka.http.model.dtos.Product
import com.datahack.akka.http.service.ProductService
import com.datahack.akka.http.service.ProductService._
import com.datahack.akka.http.utils.{Generators, SqlTestUtils}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec, WordSpecLike}

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class ProductControllerActorSpec
  extends WordSpec
    with Matchers {


  "Product Controller Actor" should {

    "get all product products" in {

    }

    "search a product by id" in {

    }

    "get ProductNotFound message if the product that we are searching by id it does not exist into database" in {

    }

    "insert a product into database" in {

    }

    "update a product stored into database" in {

    }

    "get ProductNotFound message when trying to update a product that not exit into database" in  {

    }

    "delete a product stored into database" in {

    }

    "get ProductNotFound message whe trying to delete a product that not exits" in {

    }

  }

}
