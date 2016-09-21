package consul.v1.catalog

import consul.v1.common.Types.{ServiceId, NodeId, ServiceType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class NodeProvidingService(Node: NodeId, Address: String, ServiceID: ServiceId, ServiceName: ServiceType, ServiceTags:Set[String], ServiceAddress: String, ServicePort: Int)

object NodeProvidingService {
  implicit lazy val fmt: OFormat[NodeProvidingService] = (
    (__ \ "Node").format[NodeId] and
      (__ \ "Address").format[String] and
      (__ \ "ServiceID").format[ServiceId] and
      (__ \ "ServiceName").format[ServiceType] and
      (__ \ "ServiceTags").formatNullable[Set[String]].inmap[Set[String]](_.getOrElse(Set.empty),Option(_)) and
      (__ \ "ServiceAddress").format[String] and
      (__ \ "ServicePort").format[Int]
    )(NodeProvidingService.apply _, unlift(NodeProvidingService.unapply _))
}
