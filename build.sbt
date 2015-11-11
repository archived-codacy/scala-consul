name := """scala-consul"""

version := "1.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

crossScalaVersions := Seq(scalaVersion.value)

scalacOptions ++= Seq(
  "-encoding", "UTF-8", "-deprecation", "-feature"
  ,"-Xfuture" //, "-Xverify", "-Xcheck-null"
  ,"-Ybackend:GenBCode"
  ,"-Ydelambdafy:method"
)

libraryDependencies ++= Seq(
  "org.scalatest"     %% "scalatest" % "2.2.5" % Test,
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "com.typesafe.play" %% "play-ws"   % "2.4.3",
  "com.codacy" %% "coverage-parser" % "1.1.1"
)

organization := "com.codacy"
organizationName := "Codacy"
organizationHomepage := Some(new URL("https://www.codacy.com"))

description := "Consul Scala Client"
