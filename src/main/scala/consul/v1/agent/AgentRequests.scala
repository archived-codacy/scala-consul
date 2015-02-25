package consul.v1.agent

import consul.v1.agent.check.CheckRequests
import consul.v1.agent.service.ServiceRequests
import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types
import consul.v1.common.Types._
import play.api.http.Status
import play.api.libs.json.JsObject
import play.api.libs.ws.WSRequestHolder

import scala.concurrent.{ExecutionContext, Future}

trait AgentRequests {

  def self(): Future[JsObject]
  def join(address:String,wan:Boolean=false):Future[Boolean]
  def `force-leave`(node:NodeId):Future[Boolean]
  def maintenance(enable:Boolean,reason:Option[String]):Future[Boolean]

  def service: ServiceRequests
  def check: CheckRequests
}

object AgentRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext): AgentRequests = new AgentRequests {

    def self() = erased(
      jsonRequestMaker(fullPathFor("self"), _.get())(_.validate[JsObject])
    )

    def join(address: String,wan:Boolean): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(s"join/$address"),
      (r:WSRequestHolder) => (if(wan) r.withQueryString(("wan","1")) else r).get()
    )( _ == Status.OK )

    def `force-leave`(node: Types.NodeId): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(s"force-leave/$node"),_.get()
    )( _ == Status.OK )

    def maintenance(enable:Boolean,reason:Option[String]): Future[Boolean] = {
      lazy val params = Seq(("enable",enable.toString)) ++ reason.map("reason"->_)
      responseStatusRequestMaker( maintenancePath, _.withQueryString(params:_*).get() )(_ == Status.OK)
    }

    lazy val service: ServiceRequests = ServiceRequests(currPath)

    lazy val check:CheckRequests = CheckRequests(currPath)

    private lazy val maintenancePath = fullPathFor("maintenance")

    private lazy val currPath = s"$basePath/agent"

    private def fullPathFor(path: String) = s"$currPath/$path"

  }
}
