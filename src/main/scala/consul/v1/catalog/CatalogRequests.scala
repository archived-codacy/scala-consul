package consul.v1.catalog

import consul.v1.common.Node
import consul.v1.common.Types._
import consul.v1.common.ConsulRequestBasics._
import play.api.libs.ws.WSRequestHolder
import scala.concurrent.{ExecutionContext, Future}

trait CatalogRequests {
  def nodes(dc:Option[String]=Option.empty): Future[Seq[Node]]
  def node(nodeID:NodeId,dc:Option[String]=Option.empty):Future[NodeProvidedServices]
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

    private def fullPathFor(path: String) = s"$basePath/catalog/$path"

  }

}