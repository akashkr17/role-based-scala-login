package com.util

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.models.Configurations
import io.circe._
import io.circe.syntax._
import jawn.{parse => jawnParse}
import pdi.jwt.JwtCirce
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures

//do not remove the below import i.e import pureconfig.generic.auto._
import pureconfig.generic.auto._

trait JwtTokenHelper {

  val configs: Configurations = ConfigSource
    .resources("application.conf")
    .load[Configurations] match {
    case Left(e: ConfigReaderFailures) =>
      throw new RuntimeException(
        s"Unable to load Config, original error: ${e.prettyPrint()}")
    case Right(x) => x
  }

  def setClaim(email: String, role: String): Json = {
    println("Is Role : " + Role.isUser(role))
    println("Is Role with Guest : " + Role.withGuest(role))
    jawnParse(s"""{"email":"$email","role":"${Role
      .withGuest(role)}","expires":${Instant.now
      .plus(configs.jwtScalaCirce.expireDurationHours, ChronoUnit.HOURS)
      .getEpochSecond}}""") match {
      case Right(value) => value
      case _            => "".asJson
    }
  }
  val Right(header) = jawnParse("""{"typ":"JWT","alg":"HS256"}""")
  def createToken(email: String, role: String): String = {
    JwtCirce.encode(header, setClaim(email, role), configs.jwtScalaCirce.key)
  }
}
