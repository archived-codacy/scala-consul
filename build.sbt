name := """scala-consul"""

version := "1.2.0-SNAPSHOT"

scalaVersion := "2.11.7"

crossScalaVersions := Seq(scalaVersion.value)

scalacOptions ++= Seq(
  "-encoding", "UTF-8", "-deprecation", "-feature"
  ,"-Xfuture" //, "-Xverify", "-Xcheck-null"
  ,"-Ybackend:GenBCode"
  ,"-Ydelambdafy:method"
)

resolvers += "Bintray Typesafe Repo" at "http://dl.bintray.com/typesafe/maven-releases/"

val playVersion = settingKey[String]("The version of play libraries to use")

playVersion := "2.5.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % playVersion.value,
  "com.typesafe.play" %% "play-ws"   % playVersion.value
)

organization := "com.codacy"
organizationName := "Codacy"
organizationHomepage := Some(new URL("https://www.codacy.com"))

description := "Consul Scala Client"
