# scala-consul

**Unmaintained**
> Codacy is not using consul at the moment and for that reason we are not developing this library any more. Feel free to fork this and keep your own code.

## Alternatives
* https://github.com/verizon/helm
* https://github.com/dlouwers/reactive-consul
* https://github.com/BoomTownROI/scala-consul

[![Codacy Badge](https://api.codacy.com/project/badge/grade/1edaae77fef941c39b446b6df8877183)](https://www.codacy.com/app/Codacy/scala-consul)
[![Circle CI](https://circleci.com/gh/codacy/scala-consul.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/codacy/scala-consul)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.codacy/scala-consul_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.codacy/scala-consul_2.11)

An asynchronous Scala (http://scala-lang.org/) client for Consul (https://consul.io/)

on an sbt project add this line to your build.sbt:
```scala
libraryDependencies += "com.codacy" %% "scala-consul" % "1.1.0"
```

import Consul
```scala
import consul.Consul
```

instanciate a consul supplying an ip and a port indicating a working consul agent:
```scala
val myConsul = new consul.Consul(CONSUL_IP, CONSUL_PORT, Option(CONSUL_ACL_TOKEN))
import myConsul.v1._
```

now you can use the consul API as indicated in the official documentation (http://www.consul.io/docs/agent/http.html)

Example - add a tuple to the Key/Value store:
```scala
kv.put("myKey","myValue")
```

Example - query the registered nodes:
```scala
catalog.nodes().map{ case nodes =>
    //do something with my nodes
}
```

Example - register a service with an http-check on the local node:
```scala
val myAddress = "127.0.0.1"
val myServicePort = 5000
val myServiceCheck = agent.service.httpCheck(s"http://localhost:$myServicePort/health","15s")
val myService = agent.service.LocalService(ServiceId("myServiceId"),ServiceType("myTypeOfService"),Set(ServiceTag("MyTag")),Some(myServicePort),Some(myServiceCheck),Some(Address(myAddress)))
agent.service.register(myService)
```
the check ID of the registered service-check is available via:
```scala
val myCheckId = myService.checkId
```

the other 2 types of checks are created with:
```scala
agent.service.ttlCheck
```
and
```scala
agent.service.scriptCheck
```

Error Handling:

All api methods return Futures that can fail. To parse Consul responses Play's Json library is used.
In the unlikely case that the client cannot parse the response the Future will fail and you might want to access the JsError
parsing resulted in. You can do so by recovering the Future:

```scala
import consul.v1.common.Types.ConsulResponseParseException
catalog.nodes().recover{
  case ConsulResponseParseException(jsError) =>  //do something with the JsError
  case NonFatal(otherException) => //something else
}
```

## What is Codacy?

[Codacy](https://www.codacy.com/) is an Automated Code Review Tool that monitors your technical debt, helps you improve your code quality, teaches best practices to your developers, and helps you save time in Code Reviews.

### Among Codacy’s features:

 - Identify new Static Analysis issues
 - Commit and Pull Request Analysis with GitHub, BitBucket/Stash, GitLab (and also direct git repositories)
 - Auto-comments on Commits and Pull Requests
 - Integrations with Slack, HipChat, Jira, YouTrack
 - Track issues in Code Style, Security, Error Proneness, Performance, Unused Code and other categories

Codacy also helps keep track of Code Coverage, Code Duplication, and Code Complexity.

Codacy supports PHP, Python, Ruby, Java, JavaScript, and Scala, among others.

### Free for Open Source

Codacy is free for Open Source projects.

### Licence
This software is licensed under the Apache 2 license:
http://www.apache.org/licenses/LICENSE-2.0

Copyright (C) 2014 Codacy (https://www.codacy.com)
