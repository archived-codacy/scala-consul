package consul.v1.health

import consul.v1.common.CheckStatus.CheckStatus
import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types.{NodeId, ServiceType,ServiceTag}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

trait HealthRequests{
  def node(nodeID: NodeId,dc:Option[String]=Option.empty): Future[Seq[Check]]
  def service(service: ServiceType,tag:Option[ServiceTag]=Option.empty,passing:Boolean=false,dc:Option[String]=Option.empty): Future[Seq[NodesHealthService]]
  def checks(serviceID:ServiceType,dc:Option[String]=Option.empty): Future[Seq[Check]]
  def state(state:CheckStatus,dc:Option[String]=Option.empty): Future[Seq[Check]]
}

object HealthRequests {

  implicit private val NodesHealthServiceReads: Reads[NodesHealthService] = Json.reads[NodesHealthService]

  def apply(basePath: String)(implicit executionContext: ExecutionContext): HealthRequests = new HealthRequests {

    def service(service: ServiceType, tag:Option[ServiceTag], passing:Boolean=false,dc:Option[String]): Future[Seq[NodesHealthService]] = erased{
      lazy val params = (if(passing) List(("passing","")) else List.empty ) ++ tag.map{ case tag => (("tag",tag.toString)) }

      jsonDcRequestMaker(
        fullPathFor(s"/service/$service"),dc,
        _.withQueryString(params:_*).get()
      )(_.validate[Seq[NodesHealthService]])
    }

    def node(nodeID: NodeId,dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor(s"/node/$nodeID"),dc, _.get())(_.validate[Seq[Check]])
    )

    def checks(serviceID:ServiceType,dc:Option[String]) = erased(
      jsonDcRequestMaker(fullPathFor(s"/checks/$serviceID"),dc, _.get())(_.validate[Seq[Check]])
    )

    def state(state:CheckStatus,dc:Option[String]): Future[Seq[Check]] = erased(
      jsonDcRequestMaker(fullPathFor(s"state/$state"),dc,_.get())(_.validate[Seq[Check]])
    )

    private def fullPathFor(path: String) = s"$basePath/health/$path"

  }

}
