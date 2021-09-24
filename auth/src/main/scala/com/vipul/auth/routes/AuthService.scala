package com.vipul.auth.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{
  HttpEntity,
  HttpHeader,
  HttpProtocols,
  HttpResponse,
  StatusCodes
}

import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.Multipart.{BodyPart, FormData}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import com.util.HttpUtil
import com.vipul.auth.api.UserRequest
import com.vipul.auth.handler.AuthServiceHandler

trait AuthService extends AuthServiceHandler with HttpUtil {

  def sampleRoute(actor: ActorRef): Route = {
    pathPrefix("auth") {
      path("health") {
        pathEnd {
          (post & entity(as[UserRequest])) { request =>
            logDuration(complete(addUser(actor, request)))
          }
        }
      }
    }
  }
  def routes(command: ActorRef): Route = sampleRoute(command)
}
