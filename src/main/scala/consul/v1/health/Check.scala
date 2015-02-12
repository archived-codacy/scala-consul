package consul.v1.health

import consul.v1.common.CheckStatus._
import consul.v1.common.Types.{ServiceId, CheckId}
import play.api.libs.json.{Json, Reads}

case class Check(Node: String, CheckID: CheckId, Name: String, Status: CheckStatus, Notes: String, Output: String, ServiceID: ServiceId, ServiceName: String)
object Check{
  implicit val reads: Reads[Check] = Json.reads[Check]
}