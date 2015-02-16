package consul.v1.agent.service

import consul.v1.common.Types.{CheckId, ServiceId,ServiceType,ServiceTag}
import play.api.libs.json._
import play.api.libs.json.Writes
import play.api.libs.functional.syntax._

case class LocalService(ID: ServiceId, Name: ServiceType, Tags: Set[ServiceTag] = Set.empty, Port: Option[Int], Check: Option[Check]){

  lazy val checkId:CheckId = CheckId(s"service:$ID")

}

//naming this factory so it doesn't interfere with Json.format macro....
object LocalService {
  implicit lazy val writes: Writes[LocalService] = (
      (__ \ "ID"   ).write[ServiceId]   and
      (__ \ "Name" ).write[ServiceType] and
      (__ \ "Tags" ).write[Set[ServiceTag]] and
      (__ \ "Port" ).write[Option[Int]] and
      (__ \ "Check").write[Option[Check]]
    )(  unlift(LocalService.unapply) )

  //no id is provided -> id becomes name
 def apply(Name: ServiceType, Tags: Set[ServiceTag], Port: Option[Int], Check: Option[Check]):LocalService =
    LocalService(ServiceId(Name), Name, Tags, Port, Check)
}
