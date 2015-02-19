package consul.v1.agent

import consul.v1.agent.check.CheckRequests
import consul.v1.agent.service.ServiceRequests
import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types
import consul.v1.common.Types._
import play.api.libs.json.JsObject
import play.api.libs.ws.WSRequestHolder
import play.mvc.Http.Status

import scala.concurrent.{ExecutionContext, Future}

trait AgentRequests {

  def self(): Future[JsObject]
  def join(address:String,wan:Boolean=false):Future[Boolean]
  def `force-leave`(node:NodeId):Future[Boolean]

  def service: ServiceRequests
  def check: CheckRequests
}

object AgentRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext): AgentRequests = new AgentRequests {

    def self() = erased(
      jsonRequestMaker(fullPathFor("self"), _.get())(_.validate[JsObject])
    )

    def join(address: String,wan:Boolean): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(address),
      (r:WSRequestHolder) => (if(wan) r.withQueryString(("wan","1")) else r).get()
    )( _ == Status.OK )

    def `force-leave`(node: Types.NodeId): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(node),_.get()
    )( _ == Status.OK )

    lazy val service: ServiceRequests = ServiceRequests(currPath)

    lazy val check:CheckRequests = CheckRequests(currPath)

    private lazy val currPath = s"$basePath/agent"

    private def fullPathFor(path: String) = s"$currPath/$path"

  }
}
