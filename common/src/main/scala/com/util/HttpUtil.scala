package com.util

import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging

/**
  * This object provides utility for HTTP routes
  */
trait HttpUtil extends Directives with LazyLogging {

  val rejectionHandler: RejectionHandler = RejectionHandlerFactory.newHandler()

  def logDuration(inner: Route): Route = { ctx =>
    val start = System.currentTimeMillis()
    // handling rejections here so that we get proper status codes
    val innerRejectionsHandled = handleRejections(rejectionHandler)(inner)
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      logger.info(s"[${resp.status
        .intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    }(innerRejectionsHandled)(ctx)
  }

}
