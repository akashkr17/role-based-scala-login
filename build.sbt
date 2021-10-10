import Dependencies.{Version, authModuleDependencies, commonModuleDependencies, authApiModuleDependencies,postApiModuleDependencies,postModuleDependencies}
import CommonSettings._
import scoverage._


Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

def compile(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "compile")
def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")

// the library is available in Bintray repository
resolvers += Resolver.bintrayRepo("dnvriend", "maven")

lazy val root = project.in(file("."))
  .aggregate(common, auth, authApi,post,postApi)

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
lazy val post = (
  baseProject("post")
    .dependsOn(common,postApi)
    settings(libraryDependencies ++= postModuleDependencies,
    assemblyJarName in assembly := "post.jar",
    ScoverageKeys.coverageMinimum := 80,
    ScoverageKeys.coverageFailOnMinimum := true)
  )

lazy val authApi = (
  baseProject("auth-api")
    settings(libraryDependencies ++= authApiModuleDependencies )
  )

lazy val postApi = (
  baseProject("post-api")
    settings(libraryDependencies ++= postApiModuleDependencies )
  )

envFileName in ThisBuild := ".env-service"