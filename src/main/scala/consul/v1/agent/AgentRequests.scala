package consul.v1.agent

import consul.v1.agent.check.CheckRequests
import consul.v1.agent.service.ServiceRequests
import consul.v1.common.ConsulRequestBasics._
import play.api.libs.json.JsObject

import scala.concurrent.{ExecutionContext, Future}

trait AgentRequests {

  def self(): Future[JsObject]

  def service: ServiceRequests

  def check: CheckRequests
}

object AgentRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext): AgentRequests = new AgentRequests {

    def self() = erased(
      jsonRequestMaker(fullPathFor("self"), _.get())(_.validate[JsObject])
    )

    lazy val service: ServiceRequests = ServiceRequests(currPath)

    lazy val check:CheckRequests = CheckRequests(currPath)

    private lazy val currPath = s"$basePath/agent"

    private def fullPathFor(path: String) = s"$currPath/$path"
  }
}
