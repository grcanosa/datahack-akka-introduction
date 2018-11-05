package com.datahack.akka.introduction.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.AskAdvice
import com.typesafe.config.ConfigFactory
import org.scalacheck.Gen
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class StudentSpec
  extends TestKit(ActorSystem("StudentSpec"
      ,ConfigFactory.parseString(
      """akka.loggers = ["akka.testkit.TestEventListener"]
        |akka.test.filter-leeway = 5000
      """.stripMargin)))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll  {

  val probeTeacher = TestProbe()

  val studentActor: TestActorRef[Student] = TestActorRef[Student](new Student(probeTeacher.ref))


  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher](new Teacher)
  val studentWithTeacher: TestActorRef[Student] = TestActorRef[Student](new Student(teacherActor))


  "Student Spec" should {

    "send an AskAdvice message when it receives a PerformAnAdviceRequest message" in {
      studentActor ! PerformAnAdviceRequest

      probeTeacher.expectMsgType[AskAdvice](5 seconds)
    }

    "receive an Advice when it requests and Advice on a topic the teacher knows" in {
      //Aquí no tenemos ninguna manera de hacer expect of porque es un actorref de verdad.
      //Para eso hay que pasarle al actor system un parámetro
      //Para poder mirar en los logs.
      //En el EventFilter podemos poner un mensaje exacto o un pattern

      //Sobreescribimos los topicos para asegurarnos que los topicos son los conocidos

      teacherActor.underlyingActor.advices = teacherActor.underlyingActor.advices ++
           Map[String,String]("Biology"->"Lala")

      EventFilter.info(pattern="The requested advice:*",occurrences = 1) intercept {
        studentWithTeacher ! PerformAnAdviceRequest
      }
    }

    "receive IDoNotUnderstan when the student ask for an unknown topic" in {
      teacherActor.underlyingActor.advices = Map.empty[String,String]

      EventFilter.error(message="NPI!",occurrences = 1) intercept {
        studentWithTeacher ! PerformAnAdviceRequest
      }
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
