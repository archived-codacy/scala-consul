package consul.v1.common

import play.api.libs.json.JsError

trait Types {

  sealed trait NodeIds
  type NodeId = WrappedType[String,NodeIds]
  def NodeId: String => NodeId = WrappedType.apply

  sealed trait ServiceIds
  type ServiceId = WrappedType[String,ServiceIds]
  def ServiceId: String => ServiceId = WrappedType.apply

  sealed trait CheckIds
  type CheckId = WrappedType[String,CheckIds]
  def CheckId: String => CheckId = WrappedType.apply

  sealed trait ServiceTypes
  type ServiceType = WrappedType[String,ServiceTypes]
  def ServiceType: String => ServiceType = WrappedType.apply

  sealed trait ServiceTags
  type ServiceTag = WrappedType[String,ServiceTags]
  def ServiceTag: String => ServiceTag = WrappedType.apply

  case class ConsulResponseParseException(error:JsError) extends Throwable

}

object Types extends Types

