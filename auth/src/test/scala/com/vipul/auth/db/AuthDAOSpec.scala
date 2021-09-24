package com.vipul.auth.db

import java.time.Instant

import com.vipul.auth.api.User
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AsyncWordSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.duration._

class AuthDAOSpec extends AsyncWordSpecLike with ScalaFutures with Matchers with ConfigLoader {


  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
  implicit val offsetDateTime = Instant.now()
  val authDAO = new AuthDAO()

  val user = User("id", "email", "pass", Instant.now)
  "AuthDao service" should {

    "able to add user" in {
      authDAO.addUser(user).map { res =>
        res shouldBe 1
      }
    }

  }
}
