package com.datahack.akka.http.model.dtos

/*
 * Clase que encapsula los datos de un producto
 */
case class Product(id: Option[Long], producer: String, name: String, description: String, price: Float, units: Float)
