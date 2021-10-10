package com.akash.auth.handler

import java.time.Instant
import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.akash.auth.api.{User, UserLoginRequest, UserRequest}
import com.typesafe.scalalogging.LazyLogging
import com.util.{JwtTokenHelper, ResponseUtil}
import com.akash.auth.actor.AuthActor._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

trait AuthServiceHandler extends LazyLogging with ResponseUtil with JwtTokenHelper {

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer
  implicit val actionTimeOut: Timeout = {
    Timeout(40 seconds)
  }

  import akka.pattern.ask
  import system.dispatcher

  def login(command: ActorRef, data: UserLoginRequest):Future[HttpResponse] = {
    ask(command, CheckUser(data)) map {
      case LoginSuccess(true,userId) =>
        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponse(status = true,
                  Some(List()),
                  Some(createToken(data.email,userId.getOrElse("") ,"user")),
                  Some("Login_USER"))))
        )
      case _ => HttpResponse(
        StatusCodes.OK,
        entity =
          HttpEntity(ContentTypes.`application/json`,
            write(
              generateCommonResponse(status = false,
                Some(List()),
                Some("User not found"),
                Some("Login_USER"))))
      )
    }
  }
  def addUser(command: ActorRef, userRequest: UserRequest): Future[HttpResponse] = {

    val user = User(UUID.randomUUID().toString,
      userRequest.email,
      userRequest.password,
      userRequest.name,userRequest.number,
      Instant.now())
    ask(command, AddUserRequest(user)).map {
      case m@AddUserResponse(true) =>

        HttpResponse(
          StatusCodes.OK,
          entity =
            HttpEntity(ContentTypes.`application/json`,
              write(
                generateCommonResponseForCaseClass(status = true,
                  Some(List()),
                  Some(m),
                  Some("ADD_USER"))))
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

  def getUser(command: ActorRef) : Future[HttpResponse] = {
      ask(command, GetUser()).map {
        case response: UserResponse =>
        HttpResponse(StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`,
          write(generateCommonResponseForCaseClass(status=true,None,data=Some(response),Some("GETUSER")))
          ))
        case _ => HttpResponse(StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`,
            write(generateCommonResponseForCaseClass(status=false,Some(List()),data=None,Some("GETUSER")))
          ))
      }
    }

}
