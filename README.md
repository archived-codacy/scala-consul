# scala-consul

[![Codacy Badge](https://www.codacy.com/project/badge/a4df80ffb80d4586a9153afd0e897c21)](https://www.codacy.com)

Scala (http://scala-lang.org/) client for Consul (https://consul.io/)


instanciate a consul supplying an ip and an optional port indicating a working consul agent:
```scala
val myConsul = new consul.Consul(CONSUL_IP,Option(CONSUL_PORT))
import myConsul.v1._
```

request the registered nodes: 

```scala
val nodes = catalog.nodes()
```
