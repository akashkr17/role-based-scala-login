package com.vipul.auth.handler

import java.time.Instant
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCodes
}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import com.util.ResponseUtil
import com.vipul.auth.actor.AuthActor.{AddUserRequest, AddUserResponse}
import com.vipul.auth.api.{User, UserRequest}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait AuthServiceHandler extends LazyLogging with ResponseUtil {

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer
  implicit val actionTimeOut: Timeout = {
    Timeout(40 seconds)
  }

  import akka.pattern.ask
  import system.dispatcher
  def addUser(command: ActorRef, userRequest: UserRequest) = {

    val user = User(UUID.randomUUID().toString,
                    userRequest.email,
                    userRequest.password,
                    Instant.now())
    ask(command, AddUserRequest(user)).map {
      case m @ AddUserResponse(true) =>
        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
                       write(
                         generateCommonResponseForCaseClass(status = true,
                                                            Some(List()),
                                                            Some(m),
                                                            Some("Add USer"))))
        )
      case _ =>
        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
                       write(
                         generateCommonResponseForCaseClass(status = false,
                                                            Some(List()),
                                                            None,
                                                            Some("Add USer"))))
        )
    }

  }
}
