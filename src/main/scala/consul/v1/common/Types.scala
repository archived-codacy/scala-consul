package consul.v1.common

import play.api.libs.json.JsError

trait Types {

  type NodeId = ConsulIdentifier[Node]
  def NodeId(value:String): NodeId = ConsulIdentifier[Node](value)

  type ServiceId = ConsulIdentifier[Service]
  def ServiceId(value:String): ServiceId = ConsulIdentifier[Service](value)

  trait Check
  type CheckId = ConsulIdentifier[Check]
  def CheckId(value:String): CheckId = ConsulIdentifier[Check](value)

  trait ServiceTpe
  type ServiceType = ConsulIdentifier[ServiceTpe]
  def ServiceType(value:String):ServiceType = ConsulIdentifier[ServiceTpe](value)

  case class ConsulResponseParseException(error:JsError) extends Throwable

}

object Types extends Types
