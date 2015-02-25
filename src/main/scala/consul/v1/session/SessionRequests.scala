package consul.v1.session

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types.NodeId
import consul.v1.common.JsonEnumeration
import consul.v1.common.Types.CheckId
import play.api.http.Status
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

object Behaviour extends Enumeration with JsonEnumeration{
  val release,delete = Value
}

case class SessionDef(LockDelay:Option[String]=Option.empty,Name:Option[String]=Option.empty,Node:Option[String]=Option.empty,Checks:Option[Seq[CheckId]]=Option.empty,Behavior:Option[Behaviour.Value]=Option.empty,TTL:Option[String]=Option.empty)
case class SessionInfo(LockDelay:Double,Checks:Seq[CheckId],Node:NodeId,ID:SessionId,CreateIndex:Long)
case class SessionIDHolder(ID:SessionId)
trait SessionRequests {

  def create(sessionDef:SessionDef=SessionDef(),dc:Option[String]=Option.empty):Future[SessionIDHolder]
  def destroy(id:SessionId,dc:Option[String]=Option.empty):Future[Boolean]
  def info(id:SessionId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]]
  def node(node:NodeId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]]
  def list(dc:Option[String]=Option.empty):Future[Seq[SessionInfo]]
  def renew(id:SessionId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]]
}

object SessionRequests{

  private lazy implicit val SessionIDHolderReads = Json.reads[SessionIDHolder]
  private lazy implicit val SessionInfoReads     = Json.reads[SessionInfo]
  private lazy implicit val SessionDefWrites     = Json.writes[SessionDef]

  def apply(basePath: String)(implicit executionContext: ExecutionContext): SessionRequests = new SessionRequests {

    def create(sessionDef: SessionDef,dc:Option[String]): Future[SessionIDHolder] = erased(
      jsonDcRequestMaker(
        createPath,dc,
        _.put(Json.toJson(sessionDef))
      )(_.validate[SessionIDHolder])
    )

    def node(node:NodeId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]] = erased(
      jsonDcRequestMaker(
        fullPathFor(s"node/$node"),dc,_.get
      )( _.validate[Seq[SessionInfo]] )
    )

    def destroy(id:SessionId,dc:Option[String]):Future[Boolean] = responseStatusDcRequestMaker(
      fullPathFor(s"destroy/$id"),dc,_.put("")
    )(_ == Status.OK)

    def list(dc:Option[String]):Future[Seq[SessionInfo]] = erased(
      jsonDcRequestMaker(
        listPath,dc,_.get
      )(_.validate[Seq[SessionInfo]])
    )

    def renew(id:SessionId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]] = erased(
      jsonDcRequestMaker(
        fullPathFor(s"renew/$id"),dc,_.put("")
      )(_.validate[Seq[SessionInfo]])
    )

    def info(id:SessionId,dc:Option[String]=Option.empty):Future[Seq[SessionInfo]] = erased(
      jsonDcRequestMaker(
        fullPathFor(s"info/$id"),dc,_.get
      )( _.validate[Seq[SessionInfo]] )
    )

    private lazy val createPath = fullPathFor("create")
    private lazy val listPath = fullPathFor("list")
    private def fullPathFor(path: String) = s"$basePath/session/$path"


  }

}