package consul.v1.acl

import play.api.libs.json.Json

sealed trait AclIds

case class AclCreate(Name:Option[String],Type:Option[String],Rules:Option[String])
object AclCreate { implicit lazy val aclCreateWrites = Json.writes[AclCreate] }

case class AclUpdate(ID:AclId,Name:Option[String],Type:Option[String],Rules:Option[String])
object AclUpdate { implicit lazy val aclUpdateWrites = Json.writes[AclUpdate] }

case class AclIdResponse(ID:AclId)
object AclIdResponse { implicit lazy val aclIdResponseReads = Json.reads[AclIdResponse] }

case class AclInfo(CreateIndex: Long, ModifyIndex: Long,ID:AclId,Name:Option[String],Type:Option[String],Rules:Option[String])
object AclInfo { implicit lazy val aclInfoReads = Json.reads[AclInfo] }
