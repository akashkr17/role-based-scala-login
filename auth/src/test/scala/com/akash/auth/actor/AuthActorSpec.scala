package com.akash.auth.actor

import java.time.Instant

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import AuthActor.{AddUserRequest, AddUserResponse}
import com.akash.auth.api.User
import com.akash.auth.db.AuthDAO
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.H2Profile

import scala.language.postfixOps
import io.circe.generic.auto.{exportEncoder, _}
import io.circe.syntax._
import org.mockito.MockitoSugar
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}

class AuthActorSpec (_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with AnyWordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {


  def this() = this(ActorSystem("AuthActorSystem"))

  val driver = H2Profile

  import driver.api.Database

  implicit val db: driver.api.Database = mock[Database]

  implicit val schema: String = ""
  val futureAwaitTime: FiniteDuration = 10.minute

  implicit val futureAwaitDuration: FiniteDuration =
    FiniteDuration(futureAwaitTime.length, futureAwaitTime.unit)


  val authDao: AuthDAO = mock[AuthDAO]

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val user = User("id", "email", "pass", Instant.now)

  "An Echo actor" must {

    "add user successfully" in {
      val echo = system.actorOf(Props(new AuthActor(authDao) {
        when(authDao.addUser(user)) thenReturn Future(1)
      }))
      echo ! AddUserRequest(user)
      expectMsgType[AddUserResponse](5 seconds)
    }

    "not added user successfully" in {
      val echo = system.actorOf(Props(new AuthActor(authDao) {
        when(authDao.addUser(user)) thenReturn Future(0)
      }))
      echo ! AddUserRequest(user)
      expectMsgType[AddUserResponse](5 seconds)
    }

  }
}
