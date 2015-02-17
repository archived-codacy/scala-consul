package consul.v1.common


import play.api.libs.json.JsError

trait Types {
  import consul.v1.common.Types._

  type NodeId = WrappedType[String,NodeIds]
  def NodeId: String => NodeId = WrappedType.apply
  type ServiceId = WrappedType[String,ServiceIds]
  def ServiceId: String => ServiceId = WrappedType.apply
  type CheckId = WrappedType[String,CheckIds]
  def CheckId: String => CheckId = WrappedType.apply
  type ServiceType = WrappedType[String,ServiceTypes]
  def ServiceType: String => ServiceType = WrappedType.apply
  type ServiceTag = WrappedType[String,ServiceTags]
  def ServiceTag: String => ServiceTag = WrappedType.apply

}

object Types extends Types{

  sealed trait NodeIds
  sealed trait ServiceIds
  sealed trait CheckIds
  sealed trait ServiceTypes
  sealed trait ServiceTags

  case class ConsulResponseParseException(error:JsError) extends Throwable
}

