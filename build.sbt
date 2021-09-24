import Dependencies.{Version, authModuleDependencies, commonModuleDependencies, authApiModuleDependencies}
import CommonSettings._
import scoverage._


Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

def compile(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "compile")
def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

// the library is available in Bintray repository
resolvers += Resolver.bintrayRepo("dnvriend", "maven")

lazy val root = project.in(file("."))
  .aggregate(common, auth, authApi)

lazy val common = (
  baseProject("common")
  settings(libraryDependencies ++= commonModuleDependencies)
)

lazy val auth = (
  baseProject("auth")
      .dependsOn(common,authApi)
    settings(libraryDependencies ++= authModuleDependencies,
  assemblyJarName in assembly := "auth.jar",
  ScoverageKeys.coverageMinimum := 80,
  ScoverageKeys.coverageFailOnMinimum := true)
  )

lazy val authApi = (
  baseProject("auth-api")
    settings(libraryDependencies ++= authApiModuleDependencies )
  )

envFileName in ThisBuild := ".env-service"