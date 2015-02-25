package consul.v1.agent.service

import play.api.libs.json.Json

case class Check(Script: Option[String], HTTP: Option[String],Interval: Option[String], TTL: Option[String])
object Check{

  implicit lazy val writes = Json.writes[Check]
}