package com.datahack.akka.introduction.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.acotors.{Student, Teacher}
import com.datahack.akka.introduction.acotors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.acotors.Teacher.AskAdvice
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class StudentSpec
  extends TestKit(
    ActorSystem("StudentSpec",
      ConfigFactory.parseString(
        """akka.loggers = ["akka.testkit.TestEventListener"]
          |akka.test.filter-leeway = 5000
        """.stripMargin)))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val teacherMock: TestProbe = TestProbe()
  val studentActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherMock.ref))

  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher] (new Teacher())
  val studentWithTeacherActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherActor))

  "Student Actor" should {

    "send an AskAdvice message to the Teacher Actor when it receive the PerformAndAdviceRequest message" in {
      studentActor ! PerformAnAdviceRequest

      teacherMock.expectMsgType[AskAdvice] (5 seconds)
    }

    "receive an Advice Message from Teacher Actor when it ask for it" in  {
      teacherActor.underlyingActor.advices = teacherActor.underlyingActor.advices ++
        Map[String, String] ("Biology" -> "shfksdhfksdhfksdhf")

      EventFilter.info(pattern = "The requested advice:*", occurrences = 1) intercept {
        studentWithTeacherActor ! PerformAnAdviceRequest
      }
    }

    "receive an IDoNotUnderstand message from teacher actor when it ask about a topic an the teacher do not" +
      "know anything about it" in {
      teacherActor.underlyingActor.advices = Map.empty[String, String]

      EventFilter.error(message = "Ups I do not know what happens.", occurrences = 1) intercept {
        studentWithTeacherActor ! PerformAnAdviceRequest
      }
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
