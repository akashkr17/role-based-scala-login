package com.akash.post

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.util.{HttpUtil, JwtTokenHelper, RejectionHandlerFactory}
import com.models.Configurations
import com.akash.post.db.DatabaseApi.api._
import com.akash.post.flyway._
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import slick.util.AsyncExecutor
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{cors, corsRejectionHandler}
import com.akash.post.actor.PostActor
import com.akash.post.api.Post
import com.akash.post.db.PostDAO
import com.akash.post.flyway.FlywayService
import com.akash.post.routes.PostService

import scala.collection.immutable
import scala.concurrent.duration._
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

object MainPost
  extends App
    with LazyLogging
    with PostService
    with HttpUtil {

  implicit val system: ActorSystem = ActorSystem("post")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val config: Configurations = ConfigSource
    .resources("application.conf")
    .withFallback(ConfigSource.systemProperties)
    .load[Configurations] match {
    case Left(e: ConfigReaderFailures) =>
      throw new RuntimeException(
        s"Unable to load config, original error: ${e.prettyPrint()}")
    case Right(x) => x
  }

  implicit val schema: String = config.dbConfig.schema
  implicit val db: Database = Database.forURL(
    config.dbConfig.url,
    user = config.dbConfig.user,
    password = config.dbConfig.password,
    driver = config.dbConfig.driver,
    executor = AsyncExecutor("postgres",
      numThreads = config.dbConfig.threadsPoolCount,
      queueSize = config.dbConfig.queueSize)
  )

  val futureAwaitTime: FiniteDuration =
    config.akka.futureAwaitDurationMins.minutes
  implicit val futureAwaitDuration: FiniteDuration =
    FiniteDuration(futureAwaitTime.length, futureAwaitTime.unit)

  // Migrate DB
  val flyWayService = new FlywayService(config.dbConfig)
  flyWayService.migrateDatabaseSchema()

  // create Actor
  val postDao = new PostDAO()
  val actor: ActorRef = system.actorOf(
    PostActor
      .props(postDao)
      .withRouter(RoundRobinPool(nrOfInstances = config.akka.akkaWorkersCount)),
    "post")

  val settings: CorsSettings = CorsSettings.defaultSettings.withAllowedMethods(
    immutable.Seq(
      DELETE,
      GET,
      PUT,
      POST,
      HEAD,
      OPTIONS
    ))

  lazy val routes: Route = {
    cors(settings)(
      ignoreTrailingSlash {
        pathSingleSlash {
          complete(
            HttpEntity(ContentTypes.`text/html(UTF-8)`,
              "<html><body>Hello world!</body></html>"))
        }
      } ~
        handleRejections(corsRejectionHandler.withFallback(rejectionHandler)) {
          routes(actor)
        }
    )
  }

  //bind route to server
  val binding = Http().bindAndHandle(routes, config.app.host, config.app.port)

  //scalastyle:off
  binding.onComplete {
    case Success(binding) ⇒
      val localAddress = binding.localAddress

      //scalastyle:on

      logger.info(
        s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
    case Failure(e) ⇒
      logger.error(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }
}
