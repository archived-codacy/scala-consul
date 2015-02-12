name := """scala-consul"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.6" % "test"

libraryDependencies += "com.typesafe.play"        %% "play-json"                  % "2.3.6"

libraryDependencies += "com.typesafe.play"        %% "play-ws"                    % "2.3.7"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

