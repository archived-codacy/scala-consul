package consul.v1.catalog

import consul.v1.common.{Service, Node}
import consul.v1.common.Types._

import play.api.libs.json.{Reads, Json}

case class NodeProvidedServices(Node:Node,Services:Map[String,Service]){
  lazy val numServices: Int = Services.size
  lazy val serviceTypes: Set[ServiceType] = Services.values.map(_.Service).toSet
}
object NodeProvidedServices{
  implicit lazy val reads: Reads[NodeProvidedServices] = Json.reads[NodeProvidedServices]
}