package consul.v1.health

import consul.v1.common.CheckStatus._
import consul.v1.common.Types.{ServiceId, CheckId,NodeId,ServiceType}
import play.api.libs.json.{Json, Reads}

case class Check(Node: NodeId, CheckID: CheckId, Name: String, Status: CheckStatus, Notes: String, Output: String, ServiceID: ServiceId, ServiceName: ServiceType)
object Check{
  implicit val reads: Reads[Check] = Json.reads[Check]
}