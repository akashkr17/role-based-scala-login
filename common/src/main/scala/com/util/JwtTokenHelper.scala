package com.util

import java.time.Instant
import java.time.temporal.ChronoUnit

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.directives.Credentials
import com.models.Configurations
import io.circe._
import io.circe.syntax._
import jawn.{parse => jawnParse}
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, provide}

import scala.util.Try

//do not remove the below import i.e import pureconfig.generic.auto._
import pureconfig.generic.auto._
import io.circe.generic.auto._

trait JwtTokenHelper {

  val configs: Configurations = ConfigSource
    .resources("application.conf")
    .load[Configurations] match {
    case Left(e: ConfigReaderFailures) =>
      throw new RuntimeException(
        s"Unable to load Config, original error: ${e.prettyPrint()}")
    case Right(x) => x
  }

  def setClaim(email: String,userId:String, role: String): Json = {
    println("Is Role : " + Role.isUser(role))
    println("Is Role with Guest : " + Role.withGuest(role))
    jawnParse(s"""{"email":"$email","userId":"$userId","role":"${Role
      .withGuest(role)}","expires":${Instant.now
      .plus(configs.jwtScalaCirce.expireDurationHours, ChronoUnit.HOURS)
      .getEpochSecond}}""") match {
      case Right(value) => value
      case _            => "".asJson
    }
  }
  val Right(header) = jawnParse("""{"typ":"JWT","alg":"HS256"}""")
  def createToken(email: String, userId:String,role: String): String = {
    JwtCirce.encode(header, setClaim(email,userId, role), configs.jwtScalaCirce.key)
  }

  case class TokenContent(email: String,
                          userId: String,
                          role: String)
  def decodeJwtToken(token: String)= {
    JwtCirce.decodeJson(token, configs.jwtScalaCirce.key,Seq(JwtAlgorithm.HS256))
      .toOption match {
      case Some(json) => parser.decode[TokenContent](json.toString).toOption
      case None => None
    }
  }


  def validateToken(id: String): Boolean = {
    JwtCirce.isValid(id,configs.jwtScalaCirce.key,Seq(JwtAlgorithm.HS256))

  }

  def myUserPassAuthenticator(credentials: Credentials): Option[TokenContent] =
    credentials match {
      case p @ Credentials.Provided(id) if (validateToken(id)) => decodeJwtToken(id)
      case _ => None
    }


}
