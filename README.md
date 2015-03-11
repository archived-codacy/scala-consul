# scala-consul

[![Codacy Badge](https://www.codacy.com/project/badge/a4df80ffb80d4586a9153afd0e897c21)](https://www.codacy.com)

An asynchronous Scala (http://scala-lang.org/) client for Consul (https://consul.io/)

on an sbt project add this line to your build.sbt: 
```scala
libraryDependencies += "com.codacy" %% "scala-consul" % "1.0"
```

import Consul
```scala
import consul.Consul
```

instanciate a consul supplying an ip and an optional port indicating a working consul agent:
```scala
val myConsul = new consul.Consul(CONSUL_IP,Option(CONSUL_PORT))
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
