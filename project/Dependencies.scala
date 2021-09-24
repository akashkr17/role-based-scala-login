import sbt._
object Dependencies {

  object  Version {
    val scalaV                 = "2.12.10"
    val akka                   = "2.6.16"
    val akkaHttp               = "10.2.6"
    val corsV                   = "1.1.2"
    val akkaHttpCirce          = "1.37.0"
    val akkaCaching            = "10.2.6"
    val akkaPersistence        = "2.6.16"
    val akkaPersistenceJdbc    = "3.5.3"

    val mockito                = "1.5.11"
    val ScalaTestVersion       = "3.2.9"
    val pureConfig             = "0.14.0"
    val slick                  = "3.3.3"
    val postgres               = "42.2.5"
    val slickPg                = "0.19.7"
    val hikaricp               = "3.3.3"
    val pgEmbeddedTestV        = "0.13.4"
    val persistenceTestV       = "2.5.15.2"
    val flywayCore             = "3.2.1"
    val slickCircePgV          = "0.19.7"
    val circeVersion           = "0.14.1"
    val jwtCirceV              = "9.0.1"
    val jansi                  = "1.12"
    val liftJson               = "3.3.0"
    val logback                = "1.2.3"
    val slf4j                  = "1.7.25"
    val logging                = "3.9.0"
    val oAuthV                 = "1.5.0"
  }

  object Libraries {

    val oAuth             = "com.nulab-inc" %% "scala-oauth2-core" % Version.oAuthV
    val pureConfig        = "com.github.pureconfig" %% "pureconfig" % Version.pureConfig
    val json4s            = "org.json4s" %% "json4s-native" % "3.2.11"
    val scalactic         = "org.scalactic" %% "scalactic" % Version.ScalaTestVersion
    val akkaActorTyped    = "com.typesafe.akka" %% "akka-actor-typed" % Version.akka
    val akka              = "com.typesafe.akka" %% "akka-stream-typed" % Version.akka
    val akkaHttp          = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
    val akkaHttpCirce     = "de.heikoseeberger" %% "akka-http-circe" % Version.akkaHttpCirce
    val akkaCaching       = "com.typesafe.akka" %% "akka-http-caching" % Version.akkaCaching
    val akkaSerializer    = "com.typesafe.akka" %% "akka-serialization-jackson" % Version.akka

    val akkaPersist       =  "com.typesafe.akka" %% "akka-persistence-typed" % Version.akka
    val scalaTest         = "org.scalatest" %% "scalatest" % Version.ScalaTestVersion % Test

    val mockito            = "org.mockito" %% "mockito-scala-scalatest" % Version.mockito
    val mock               = "org.mockito" % "mockito-core" % "3.6.0" % Test
    val akkaTest           = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akka % Test
    val akkaStreamTest    =  "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka % Test
    val akkaPersistTest   = "com.typesafe.akka" %% "akka-persistence-testkit" % Version.akka % Test


    val cors              = "ch.megard" %% "akka-http-cors" % Version.corsV
    //flyway
    val flyway            =  "org.flywaydb" % "flyway-core" % Version.flywayCore
    val slick             = "com.typesafe.slick" %% "slick" % Version.slick
    val postgres          = "org.postgresql" % "postgresql" % Version.postgres
    val slickPg           = "com.github.tminglei" %% "slick-pg" % Version.slickPg
    val hikaricp          = "com.typesafe.slick" %% "slick-hikaricp" % Version.hikaricp
    val otjPGEmbedded     = "com.opentable.components" % "otj-pg-embedded" % Version.pgEmbeddedTestV % Test

    val slickCirce        =  "com.github.tminglei" %% "slick-pg_circe-json" % Version.slickCircePgV
    // circe for paring
    val circe: Seq[ModuleID] = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % Version.circeVersion)

    val slf4j           = "org.slf4j" % "slf4j-simple" % Version.slf4j
    val logging         = "com.typesafe.scala-logging" %% "scala-logging" % Version.logging
    val logback         = "ch.qos.logback" % "logback-classic" % Version.logback
    val jwtCirci          =  "com.github.jwt-scala" %% "jwt-circe" % Version.jwtCirceV
    val jansi           = "org.fusesource.jansi" % "jansi" % Version.jansi
    val liftJson         =  "net.liftweb" %% "lift-json" % Version.liftJson
  }


  val akkaDependencies: Seq[ModuleID] = Seq(Libraries.akkaHttp, Libraries.akka, Libraries.akkaHttpCirce,
    Libraries.akkaCaching, Libraries.cors)
  val logDependencies: Seq[ModuleID] = Seq(Libraries.logging, Libraries.slf4j, Libraries.pureConfig)

  val dbDependencies = Seq(Libraries.slick, Libraries.slickPg, Libraries.postgres, Libraries.hikaricp,
    Libraries.slickCirce, Libraries.otjPGEmbedded)

  val commonModuleDependencies: Seq[sbt.ModuleID] = akkaDependencies ++ logDependencies ++
    Seq(Libraries.jansi,  Libraries.liftJson, Libraries.flyway, Libraries.jwtCirci, Libraries.mockito) ++ Libraries.circe

  val authApiModuleDependencies: Seq[sbt.ModuleID] = Seq(Libraries.liftJson ) ++ Libraries.circe

  val authModuleDependencies: Seq[sbt.ModuleID] = akkaDependencies ++ logDependencies ++ dbDependencies ++
    Seq( Libraries.akkaTest,Libraries.scalaTest, Libraries.cors, Libraries.mockito, Libraries.mock ,Libraries.akkaPersistTest)
}
