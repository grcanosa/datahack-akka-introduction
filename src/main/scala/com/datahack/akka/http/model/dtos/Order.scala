package com.datahack.akka.http.model.dtos

/*
 * Clase que encapsula los datos de una orden de compra
 */
case class Order(requestId: Option[String], productId: Long, quantity: Float)
