# scala-consul

[![Codacy Badge](https://api.codacy.com/project/badge/grade/1edaae77fef941c39b446b6df8877183)](https://www.codacy.com/app/Codacy/scala-consul)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/1edaae77fef941c39b446b6df8877183)](https://www.codacy.com/app/Codacy/scala-consul)
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
val myConsul = new consul.Consul(CONSUL_IP, CONSUL_PORT)
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
val myServicePort = 5000
val myServiceCheck = agent.service.httpCheck(s"http://localhost:$myServicePort/health","15s")
val myService = agent.service.LocalService(ServiceId("myServiceId"),ServiceType("myTypeOfService"),Set(ServiceTag("MyTag")),Some(myServicePort),Some(myServiceCheck))
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

### Licence
This software is licensed under the Apache 2 license:
http://www.apache.org/licenses/LICENSE-2.0

Copyright (C) 2014 Codacy (https://www.codacy.com)
