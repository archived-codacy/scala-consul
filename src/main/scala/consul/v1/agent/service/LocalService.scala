package consul.v1.agent.service

import consul.v1.common.Types.{Address, CheckId, ServiceId, ServiceTag, ServiceType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class LocalService(ID: ServiceId, Name: ServiceType, Tags: Set[ServiceTag] = Set.empty, Port: Option[Int], Check: Option[Check],
                        Address: Option[Address] = None){
  lazy val checkId:CheckId = CheckId(s"service:$ID")
}

object LocalService {
  implicit lazy val writes: OWrites[LocalService] = (
      (__ \ "ID"   ).write[ServiceId]   and
      (__ \ "Name" ).write[ServiceType] and
      (__ \ "Tags" ).write[Set[ServiceTag]] and
      (__ \ "Port" ).write[Option[Int]] and
      (__ \ "Check").write[Option[Check]] and
      (__ \ "Address").write[Option[Address]]
    )(  unlift(LocalService.unapply) )

  //no id is provided -> id becomes name
 def apply(Name: ServiceType, Tags: Set[ServiceTag], Port: Option[Int], Check: Option[Check]):LocalService =
    LocalService(ServiceId(Name), Name, Tags, Port, Check)
}
