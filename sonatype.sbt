// Your profile name of the sonatype account. The default is the same with the organization value
sonatypeProfileName := "com.sandinh"

pomExtra in Global := {
  <url>https://github.com/giabao/scala-consul</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/giabao/scala-consul</url>
    <connection>scm:git:git@github.com:giabao/scala-consul.git</connection>
    <developerConnection>scm:git:https://github.com/giabao/scala-consul.git</developerConnection>
  </scm>
  <developers>
    <developer>
        <id>mrfyda</id>
        <name>Rafael</name>
        <email>rafael [at] codacy.com</email>
        <url>https://github.com/mrfyda</url>
      </developer>
    <developer>
      <id>giabao</id>
      <name>San Dinh</name>
      <url>http://sandinh.com</url>
    </developer>
  </developers>
}
