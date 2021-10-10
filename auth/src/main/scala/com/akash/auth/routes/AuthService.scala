package com.akash.auth.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, HttpHeader, HttpProtocols, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.Multipart.{BodyPart, FormData}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import com.akash.auth.api.{UserLoginRequest, UserRequest}
import com.akash.auth.handler.AuthServiceHandler
import com.util.HttpUtil

trait AuthService extends AuthServiceHandler with HttpUtil {

  def sampleRoute(actor: ActorRef): Route = {
    pathPrefix("auth") {
      path("user") {
        pathEnd {
          (post & entity(as[UserRequest])) { request =>
            logDuration(complete(addUser(actor, request)))
          }
        }
      } ~
        path("user") {
          get {
            pathEnd {
              logDuration(complete(getUser(actor)))
            }
          }
        } ~
        path("user-login") {
          (post & entity(as[UserLoginRequest])) { req =>
            pathEnd {
              logDuration(complete(login(actor,req)))
            }
          }
        }
        }

    }

  def routes(command: ActorRef): Route = sampleRoute(command)
}
