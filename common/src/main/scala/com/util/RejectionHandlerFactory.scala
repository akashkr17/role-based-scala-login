package com.util

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._
object RejectionHandlerFactory {
  def newHandler(): RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case AuthorizationFailedRejection =>
          complete(Forbidden, "You're out of your depth!")
      }
      .handle {
        case ValidationRejection(msg, _) =>
          complete(InternalServerError, "That wasn't valid! " + msg)
      }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete(MethodNotAllowed,
                 s"Can't do that! Supported: ${names mkString " or "}!")
      }
      .handleNotFound { complete((NotFound, "Not here!")) }
      .result()
      .withFallback(RejectionHandler.default)
      .mapRejectionResponse {
        case res @ HttpResponse(_, _, ent: HttpEntity.Strict, _) =>
          // since all Akka default rejection responses are Strict this will handle all rejections
          val message = ent.data.utf8String.replaceAll("\"", """\"""")

          // we copy the response in order to keep all headers and status code, wrapping the message as hand rolled JSON
          // you could the entity using your favourite marshalling library (e.g. spray json or anything else)
          res.withEntity(
            HttpEntity(ContentTypes.`application/json`,
                       s"""{"rejection": "$message"}"""))

        case x => x // pass through all other types of responses
      }
}
