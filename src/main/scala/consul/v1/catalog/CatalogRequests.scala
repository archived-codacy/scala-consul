package consul.v1.catalog

import consul.v1.common.{Types, Node}
import consul.v1.common.Types._
import consul.v1.common.ConsulRequestBasics._
import play.api.libs.ws.WSRequestHolder
import scala.concurrent.{ExecutionContext, Future}

trait CatalogRequests {
  /* /v1/catalog/register   : Registers a new node, service, or check
     /v1/catalog/deregister : Deregisters a node, service, or check */
  def datacenters(): Future[Seq[String]]
  def nodes(dc:Option[String]=Option.empty): Future[Seq[Node]]
  def node(nodeID:NodeId,dc:Option[String]=Option.empty):Future[NodeProvidedServices]
  def services(dc:Option[String]=Option.empty):Future[Map[ServiceType,Set[String]]]
  def service(service:ServiceType,tag:Option[String]=Option.empty, dc:Option[String]=Option.empty):Future[Seq[NodeProvidingService]]
}

object CatalogRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext): CatalogRequests = new CatalogRequests {

    def nodes(dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor("nodes"),dc, _.get())(_.validate[Seq[Node]])
    )

    def node(nodeID: NodeId, dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor(s"node/$nodeID"),dc, _.get())(_.validate[NodeProvidedServices])
    )

    def service(service: ServiceType, tag:Option[String], dc:Option[String]) = erased(
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
    private lazy val servicesPath = fullPathFor("services")

    private def fullPathFor(path: String) = s"$basePath/catalog/$path"

  }

}
