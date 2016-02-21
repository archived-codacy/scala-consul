package consul.v1.health

import consul.v1.common.CheckStatus.CheckStatus
import consul.v1.common.ConsulRequestBasics
import consul.v1.common.Types.{NodeId, ServiceType,ServiceTag,DatacenterId}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

trait HealthRequests{
  def node(nodeID: NodeId,dc:Option[DatacenterId]=Option.empty): Future[Seq[Check]]
  def service(service: ServiceType,tag:Option[ServiceTag]=Option.empty,passing:Boolean=false,dc:Option[DatacenterId]=Option.empty): Future[Seq[NodesHealthService]]
  def checks(serviceID:ServiceType,dc:Option[DatacenterId]=Option.empty): Future[Seq[Check]]
  def state(state:CheckStatus,dc:Option[DatacenterId]=Option.empty): Future[Seq[Check]]
}

object HealthRequests {

  implicit private val NodesHealthServiceReads: Reads[NodesHealthService] = Json.reads[NodesHealthService]

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): HealthRequests = new HealthRequests {
    def service(service: ServiceType, tag:Option[ServiceTag], passing:Boolean=false,dc:Option[DatacenterId]): Future[Seq[NodesHealthService]] = rb.erased{
      lazy val params = (if(passing) List(("passing","")) else List.empty ) ++ tag.map{ case tag => (("tag",tag.toString)) }

      rb.jsonDcRequestMaker(
        s"/health/service/$service",dc,
        _.withQueryString(params:_*).get()
      )(_.validate[Seq[NodesHealthService]])
    }

    def node(nodeID: NodeId,dc:Option[DatacenterId]) = rb.erased(
      rb.jsonDcRequestMaker(s"/health/node/$nodeID",dc, _.get())(_.validate[Seq[Check]])
    )

    def checks(serviceID:ServiceType,dc:Option[DatacenterId]) = rb.erased(
      rb.jsonDcRequestMaker(s"/health/checks/$serviceID",dc, _.get())(_.validate[Seq[Check]])
    )

    def state(state:CheckStatus,dc:Option[DatacenterId]): Future[Seq[Check]] = rb.erased(
      rb.jsonDcRequestMaker(s"/health/state/$state",dc,_.get())(_.validate[Seq[Check]])
    )

  }

}
