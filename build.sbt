name := """scala-consul"""

version := "1.0.1"

scalaVersion := "2.11.6"

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

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

startYear := Some(2014)

description := "Consul Scala Client"

licenses := Seq("GNU GENERAL PUBLIC LICENSE, Version 3.0" -> url("http://www.gnu.org/licenses/gpl-3.0.txt"))

homepage := Some(url("https://github.com/codacy/scala-consul"))

pomExtra :=
  <scm>
    <url>https://github.com/codacy/scala-consul</url>
    <connection>scm:git:git@github.com:codacy/scala-consul.git</connection>
    <developerConnection>scm:git:https://github.com/codacy/scala-consul.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>mrfyda</id>
        <name>Rafael</name>
        <email>rafael [at] codacy.com</email>
        <url>https://github.com/mrfyda</url>
      </developer>
    </developers>
