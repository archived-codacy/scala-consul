name := """scala-consul"""

version := "1.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

crossScalaVersions  := Seq("2.10.5", "2.11.6")

resolvers ++= Seq(
  DefaultMavenRepository,
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases
)

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest"     %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.9"

libraryDependencies += "com.typesafe.play" %% "play-ws"   % "2.3.9"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

organization := "com.codacy"
organizationName := "Codacy"
organizationHomepage := Some(new URL("https://www.codacy.com"))

description := "Consul Scala Client"
