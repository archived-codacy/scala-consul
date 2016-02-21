package consul.v1.catalog

import consul.v1.common.CheckStatus._
import consul.v1.common.Types._
import consul.v1.common.{ConsulRequestBasics, Node, Types}
import play.api.http.Status
import play.api.libs.json._
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}

case class Deregisterable(Node:NodeId,ServiceID:Option[ServiceId],CheckID:Option[CheckId],Datacenter:Option[DatacenterId])
case class Service(ID:ServiceId,Service:ServiceType,Tags:Set[ServiceTag],Address:Option[String],Port:Option[Int])
case class Check(Node:NodeId,CheckID:CheckId,Name:String,Notes:Option[String],Status:CheckStatus,ServiceID:Option[ServiceId])
case class Registerable(Node:NodeId,Address:String,Service:Option[Service],Check:Option[Check],Datacenter:Option[DatacenterId])

trait CatalogRequests {
  def register(registerable:Registerable):Future[Boolean]
  def deregister(deregisterable:Deregisterable):Future[Boolean]
  def datacenters(): Future[Seq[DatacenterId]]
  def nodes(dc:Option[DatacenterId]=Option.empty): Future[Seq[Node]]
  def node(nodeID:NodeId,dc:Option[DatacenterId]=Option.empty):Future[NodeProvidedServices]
  def services(dc:Option[DatacenterId]=Option.empty):Future[Map[ServiceType,Set[String]]]
  def service(service:ServiceType,tag:Option[ServiceTag]=Option.empty, dc:Option[DatacenterId]=Option.empty):Future[Seq[NodeProvidingService]]

  /*convenience methods*/
  def deregisterNode(node:NodeId,dc:Option[DatacenterId]): Future[Boolean] =
    deregister(Deregisterable(node,Option.empty,Option.empty,dc))
  def deregisterService(service:ServiceId,node:NodeId,dc:Option[DatacenterId]): Future[Boolean] =
    deregister(Deregisterable(node,Option(service),Option.empty,dc))
  def deregisterCheck(check:CheckId,node:NodeId,dc:Option[DatacenterId]): Future[Boolean] =
    deregister(Deregisterable(node,Option.empty,Option(check),dc))

  def Registerable: (Types.NodeId, String, Option[Service], Option[Check], Option[Types.DatacenterId]) => Registerable =
    consul.v1.catalog.Registerable.apply _
  def Check: (Types.NodeId, Types.CheckId, String, Option[String], CheckStatus, Option[Types.ServiceId]) => Check =
    consul.v1.catalog.Check.apply _
  def Service: (Types.ServiceId, Types.ServiceType, Set[Types.ServiceTag], Option[String], Option[Int]) => Service =
    consul.v1.catalog.Service.apply _
  def Deregisterable = consul.v1.catalog.Deregisterable.apply _

}

object CatalogRequests {

  private implicit lazy val deregisterWrites = Json.writes[Deregisterable]
  private implicit lazy val registerWrites   = {
    implicit val serviceWrites = Json.writes[Service]
    implicit val checkWrites = Json.writes[Check]

    Json.writes[Registerable]
  }

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): CatalogRequests = new CatalogRequests {

    def register(registerable: Registerable): Future[Boolean] = rb.responseStatusRequestMaker(
      "/catalog/register",
      _.put( Json.toJson(registerable) )
    )(_ == Status.OK)

    def deregister(deregisterable:Deregisterable):Future[Boolean] = rb.responseStatusRequestMaker(
      "/catalog/deregister",
      _.put(Json.toJson(deregisterable))
    )(_ == Status.OK)

    def nodes(dc:Option[DatacenterId]) = rb.erased(
      rb.jsonDcRequestMaker("/catalog/nodes",dc, _.get())(_.validate[Seq[Node]])
    )

    def node(nodeID: NodeId, dc:Option[DatacenterId]) = rb.erased(
      rb.jsonDcRequestMaker(s"/catalog/node/$nodeID",dc, _.get())(_.validate[NodeProvidedServices])
    )

    def service(service: ServiceType, tag:Option[ServiceTag], dc:Option[DatacenterId]) = rb.erased(
      rb.jsonDcRequestMaker(s"/catalog/service/$service",dc,
        (r:WSRequest) => tag.map{ case tag => r.withQueryString("tag"->tag) }.getOrElse(r).get()
      )(_.validate[Seq[NodeProvidingService]])
    )

    def datacenters(): Future[Seq[DatacenterId]] = rb.erased(
      rb.jsonRequestMaker("/catalog/datacenters", _.get() )(_.validate[Seq[DatacenterId]])
    )

    def services(dc:Option[DatacenterId]=Option.empty): Future[Map[Types.ServiceType, Set[String]]] = rb.erased(
      rb.jsonDcRequestMaker("/catalog/services", dc, _.get())(
        _.validate[Map[String,Set[String]]].map(_.map{ case (key,value) => ServiceType(key)->value })
      )
    )

  }

}
