package consul.v1.acl

import consul.v1.common.ConsulRequestBasics
import play.api.http.Status
import play.api.libs.json.{JsNull, Json}

import scala.concurrent.{ExecutionContext, Future}

trait AclRequests {
  def create(Name:Option[String]=Option.empty,Type:Option[String]=Option.empty,Rules:Option[String]=Option.empty):Future[AclIdResponse]
  def create(acl:AclCreate):Future[AclIdResponse]
  def update(ID:AclId,Name:Option[String]=Option.empty,Type:Option[String]=Option.empty,Rules:Option[String]=Option.empty):Future[Boolean]
  def update(acl:AclUpdate):Future[Boolean]
  def destroy(id:AclId):Future[Boolean]
  def info(id:AclId):Future[Option[AclInfo]]
  def clone(id:AclId): Future[AclIdResponse]
  def list():Future[Seq[AclInfo]]

  def AclCreate = consul.v1.acl.AclCreate.apply _
  def AclUpdate = consul.v1.acl.AclUpdate.apply _
  def AclId     = consul.v1.acl.AclId
}

object AclRequests{

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): AclRequests = new AclRequests{

    def create(acl:AclCreate):Future[AclIdResponse] = rb.erased(
      rb.jsonRequestMaker("/acl/create",_.put(Json.toJson(acl)))(_.validate[AclIdResponse])
    )

    def create(Name:Option[String],Type:Option[String],Rules:Option[String]):Future[AclIdResponse] = create(
      AclCreate(Name,Type,Rules)
    )

    def update(acl: AclUpdate): Future[Boolean] =
      rb.responseStatusRequestMaker("/acl/update",_.put(Json.toJson(acl)))(_ == Status.OK)

    def update(ID:AclId,Name:Option[String],Type:Option[String],Rules:Option[String]): Future[Boolean] =
      update(AclUpdate(ID,Name,Type,Rules))

    def destroy(id:AclId):Future[Boolean] =
      rb.responseStatusRequestMaker(s"/acl/destroy/$id",_.put(JsNull))(_ == Status.OK)

    def list():Future[Seq[AclInfo]] = rb.erased(
      rb.jsonRequestMaker("/acl/list",_.get())(_.validate[Seq[AclInfo]])
    )

    def info(id:AclId): Future[Option[AclInfo]] = rb.erased(
      rb.jsonRequestMaker(s"/acl/info/$id",_.get())(_.validateOpt[AclInfo])
    )

    def clone(id:AclId): Future[AclIdResponse] = rb.erased(
      rb.jsonRequestMaker(s"/acl/clone/$id",_.put(JsNull))(_.validate[AclIdResponse])
    )

  }
}