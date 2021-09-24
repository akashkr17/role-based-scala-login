import Dependencies.Version
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.{MergeStrategy, PathList}
import scoverage.ScoverageKeys
import sbtsonar.SonarPlugin.autoImport.sonarProperties

object CommonSettings {

  lazy val projectSettings = Seq(
    name := "role-based-login-scala",
      version := "0.1",
  scalaVersion := Version.scalaV,
    parallelExecution in Test := false,
    checksums in update := Nil,
    ScoverageKeys.coverageExcludedFiles := ".*MainAuth.*;.*FlywayService.*;" + ".*FailurePropatingActor.*;",
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) =>
        (xs map {
          _.toLowerCase
        }) match {
          case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) => MergeStrategy.discard
          case _ => MergeStrategy.first
        }
      case "application.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
  )
  def baseProject(name: String): Project = {
    Project(name, file(name))
  }

  // TODO this is currently hard coded, will check how to pass the path dynamically.
  //scalastyle:off
  lazy val sonarSettings: Def.Setting[Map[String, String]] = {
    sonarProperties ++= Map(
      "sonar.modules" -> "auth,common,auth-api",
      "sonar.sourceEncoding" -> "UTF-8",
      "auth.sonar.projectName" -> "auth",
       "sonar.coverage.exclusions" -> "**/*MainAuth.*,**/*FlywayService.*,**/*FailurePropatingActor.*"

    )
  }
}
