package com.datahack.akka.introduction.actors

import com.datahack.akka.introduction.actors.Teacher.{AskAdvice, IDoNotUnderstand, _}
import com.datahack.akka.introduction.actors.Student._
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TeacherSpec
  extends TestKit(ActorSystem("TeacherSpec"))
   with WordSpecLike
   with Matchers
   with BeforeAndAfterAll {

  val teacherActor = TestActorRef[Teacher](new Teacher)

  "Teacher Actor" should {
    "send a response message to the sender of the message AskAdvice" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      teacherActor ! AskAdvice("Maths")

      sender.expectMsg[Advice](Advice("Anything worth doing is worth overdoing"))

    }

    "send IDoNotUnderstand response to the sender of the message AskAdvice when the topic is unknown" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      teacherActor ! AskAdvice("Robotica")

      sender.expectMsg(IDoNotUnderstand)
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
