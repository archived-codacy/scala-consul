package consul.v1.session

import consul.v1.common.ConsulRequestBasics
import consul.v1.common.Types._
import play.api.http.Status
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

trait SessionRequests {

  def create(sessionDef:SessionDef=SessionDef(),dc:Option[DatacenterId]=Option.empty):Future[SessionIDHolder]
  def destroy(id:SessionId,dc:Option[DatacenterId]=Option.empty):Future[Boolean]
  def info(id:SessionId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]]
  def node(node:NodeId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]]
  def list(dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]]
  def renew(id:SessionId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]]

  def SessionDef(LockDelay:Option[String]=Option.empty, Name:Option[String]=Option.empty,
                 Node:Option[String]=Option.empty, Checks:Option[Seq[CheckId]]=Option.empty,
                 Behavior:Option[Behaviour.Value]=Option.empty, TTL:Option[String]=Option.empty): SessionDef =
    consul.v1.session.SessionDef(LockDelay,Name,Node,Checks,Behavior,TTL)
  def SessionId: (String) => SessionId = consul.v1.session.SessionId
  lazy val Behavior: Behaviour.type = consul.v1.session.Behaviour
}

object SessionRequests{

  private lazy implicit val SessionIDHolderReads = Json.reads[SessionIDHolder]
  private lazy implicit val SessionInfoReads     = Json.reads[SessionInfo]
  private lazy implicit val SessionDefWrites     = Json.writes[SessionDef]

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): SessionRequests = new SessionRequests{

    def create(sessionDef: SessionDef,dc:Option[DatacenterId]): Future[SessionIDHolder] = rb.erased(
      rb.jsonDcRequestMaker(
        "/session/create",dc,
        _.put(Json.toJson(sessionDef))
      )(_.validate[SessionIDHolder])
    )

    def node(node:NodeId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]] = rb.erased(
      rb.jsonDcRequestMaker(
        s"/session/node/$node",dc,_.get
      )( _.validate[Seq[SessionInfo]] )
    )

    def destroy(id:SessionId,dc:Option[DatacenterId]):Future[Boolean] = rb.responseStatusDcRequestMaker(
      s"/session/destroy/$id",dc,_.put("")
    )(_ == Status.OK)

    def list(dc:Option[DatacenterId]):Future[Seq[SessionInfo]] = rb.erased(
      rb.jsonDcRequestMaker(
        "/session/list",dc,_.get
      )(_.validate[Seq[SessionInfo]])
    )

    def renew(id:SessionId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]] = rb.erased(
      rb.jsonDcRequestMaker(
        s"/session/renew/$id",dc,_.put("")
      )(_.validate[Seq[SessionInfo]])
    )

    def info(id:SessionId,dc:Option[DatacenterId]=Option.empty):Future[Seq[SessionInfo]] = rb.erased(
      rb.jsonDcRequestMaker(
        s"/session/info/$id",dc,_.get
      )( _.validate[Seq[SessionInfo]] )
    )

  }

}