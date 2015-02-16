package consul.v1.catalog

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types._
import consul.v1.common.{Node, Types}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.WSRequestHolder

import scala.concurrent.{ExecutionContext, Future}

trait CatalogRequests {
  //def register(node:Node, service:Option[Service], check:Option[Check], dc:Option[String]=Option.empty):Future[Boolean]
  /*def deregister(node:NodeId,dc:Option[String]=Option.empty):Future[Boolean]
  def deregister(check:CheckId,node:NodeId,dc:Option[String]=Option.empty):Future[Boolean]
  def deregister(service:ServiceId,node:NodeId,dc:Option[String]=Option.empty):Future[Boolean]*/
  def datacenters(): Future[Seq[String]]
  def nodes(dc:Option[String]=Option.empty): Future[Seq[Node]]
  def node(nodeID:NodeId,dc:Option[String]=Option.empty):Future[NodeProvidedServices]
  def services(dc:Option[String]=Option.empty):Future[Map[ServiceType,Set[String]]]
  def service(service:ServiceType,tag:Option[ServiceTag]=Option.empty, dc:Option[String]=Option.empty):Future[Seq[NodeProvidingService]]
}

object CatalogRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext): CatalogRequests = new CatalogRequests {

    /*def register(node: Node, service:Option[Service], check:Option[Check], dc:Option[String]): Future[Boolean] = responseStatusDcRequestMaker(
      registerPath,dc,
      _.put( regW.writes(service,node,check,dc) )
    )(_ == Status.OK)*/
/*
    def deregister(node: Types.NodeId, dc: Option[String]): Future[Boolean] = responseStatusDcRequestMaker(
      deregisterPath,dc,
      _.put(nodeDeregW.writes(node,dc))
    )(_ == Status.OK)

    def deregister(check: Types.CheckId, node: Types.NodeId, dc: Option[String]): Future[Boolean] = responseStatusDcRequestMaker(
      deregisterPath,dc,
      _.put(checkDeregW.writes(node,dc,check))
    )(_ == Status.OK)

    def deregister(service: Types.ServiceId, node: Types.NodeId, dc: Option[String]): Future[Boolean] = responseStatusDcRequestMaker(
      deregisterPath,dc,
      _.put(serviceDeregW.writes(node,dc,service))
    )(_ == Status.OK)*/

    def nodes(dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor("nodes"),dc, _.get())(_.validate[Seq[Node]])
    )

    def node(nodeID: NodeId, dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor(s"node/$nodeID"),dc, _.get())(_.validate[NodeProvidedServices])
    )

    def service(service: ServiceType, tag:Option[ServiceTag], dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor(s"service/$service"),dc,
        (r:WSRequestHolder) => tag.map{ case tag => r.withQueryString("tag"->tag) }.getOrElse(r).get()
      )(_.validate[Seq[NodeProvidingService]])
    )

    def datacenters(): Future[Seq[String]] = erased(
      jsonRequestMaker(datacenterPath, _.get() )(_.validate[Seq[String]])
    )

    def services(dc:Option[String]=Option.empty): Future[Map[Types.ServiceType, Set[String]]] = erased(
      jsonDcRequestMaker(servicesPath, dc, _.get())(
        _.validate[Map[String,Set[String]]].map(_.map{ case (key,value) => ServiceType(key)->value })
      )
    )

    private lazy val datacenterPath = fullPathFor("datacenters")
    private lazy val servicesPath   = fullPathFor("services")
    private lazy val registerPath   = fullPathFor("register")
    private lazy val deregisterPath = fullPathFor("deregister")

    private def fullPathFor(path: String) = s"$basePath/catalog/$path"

    private lazy val n = (__ \ "Node"      ).write[NodeId]
    private lazy val d = (__ \ "Datacenter").write[Option[String]]
    private lazy val c = (__ \ "CheckID"   ).write[CheckId]
    private lazy val s = (__ \ "ServiceID" ).write[ServiceId]

    //private lazy val serviceRegW = (__ \ "Service").writeNullable[Service]
    //private lazy val checkRegW   = (__ \ "Check").writeNullable[Check]
    //private lazy val regW        = serviceRegW and Node.fmt and checkRegW and d tupled

    private lazy val nodeDeregW:    OWrites[(Types.NodeId, Option[String])] = n and d tupled
    private lazy val checkDeregW:   OWrites[(Types.NodeId, Option[String], Types.CheckId)] = n and d and c tupled
    private lazy val serviceDeregW: OWrites[(Types.NodeId, Option[String], Types.ServiceId)] = n and d and s tupled

  }

}
