package consul.v1.catalog

import consul.v1.common.{Service => GService, Node}
import consul.v1.common.Types._
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class NodeProvidedServices(Node:Node,Services:Map[ServiceType,GService]){
  lazy val numServices: Int = Services.size
  lazy val serviceTypes: Set[ServiceType] = Services.values.map(_.Service).toSet
}
object NodeProvidedServices{

  implicit val reads: Reads[NodeProvidedServices] = (
    ( __ \ "Node").read[Node] and
    ( __ \ "Services").read[Map[String,GService]].map(_.map{ case (key,value) => ServiceType(key)->value })
  )( NodeProvidedServices.apply _)

}