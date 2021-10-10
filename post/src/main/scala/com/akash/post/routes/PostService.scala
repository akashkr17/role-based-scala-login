package com.akash.post.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, HttpHeader, HttpProtocols, HttpResponse, StatusCodes}

import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import akka.http.scaladsl.model.Multipart.{BodyPart, FormData}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.FileIO
import com.akash.post.api.{GetPostRequest, PostRequest, UpdatePostRequest}
import com.akash.post.handler.PostServiceHandler
import com.util.{HttpUtil, JwtTokenHelper}

trait PostService extends PostServiceHandler with HttpUtil with JwtTokenHelper{

  def sampleRoute(actor: ActorRef): Route = pathPrefix("posts") {
    path("post") {
      pathEnd {
          (post & entity(as[PostRequest])) { request =>
            authenticateOAuth2("Bearer Authentication", myUserPassAuthenticator) { user => println(user)
              logDuration(complete(addPost(actor, request)))
          }
        }
      }
    }~
    path("post") {
      pathEnd {
        (put & entity(as[UpdatePostRequest])) { request =>
          logDuration(complete(updatePost(actor,request)))
        }
      }
    } ~
      path("post") {
        get {
          pathEnd {
            logDuration(complete(getPosts(actor)))
          }
        }
      } ~
     path("delete-post" / Segment) { id =>
       delete {
            logDuration(complete(deletePost(actor, id)))
          }
    } ~
      path("user-post" / Segment) { id =>
         get {
            logDuration(complete(getPostsByUserId(actor, id)))
            }
      }
  }

  def routes(command: ActorRef): Route = sampleRoute(command)
}
