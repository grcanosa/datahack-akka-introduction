package com.datahack.akka.http.model.dtos

/*
 * Clase que encapsula los datos de un usuario
 */
case class User(id: Option[Long], name: String, email: String, password: String)
