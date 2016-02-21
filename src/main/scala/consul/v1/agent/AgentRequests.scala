package consul.v1.agent

import consul.v1.agent.check.CheckRequests
import consul.v1.agent.service.ServiceRequests

import consul.v1.common.{ConsulRequestBasics, Service, Types}
import consul.v1.common.Types._
import consul.v1.health.Check
import play.api.http.Status
import play.api.libs.json.{JsNull, JsObject}
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}

trait AgentRequests {

  def self(): Future[JsObject]
  def join(address:String,wan:Boolean=false):Future[Boolean]
  def `force-leave`(node:NodeId):Future[Boolean]
  def maintenance(enable:Boolean,reason:Option[String]):Future[Boolean]

  def checks():Future[Map[CheckId,Check]]
  def services():Future[Map[ServiceId,Service]]


  def service: ServiceRequests
  def check: CheckRequests
}

object AgentRequests {

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): AgentRequests = new AgentRequests {

    def self() = rb.erased(
      rb.jsonRequestMaker("/agent/self", _.get())(_.validate[JsObject])
    )

    def join(address: String,wan:Boolean): Future[Boolean] = rb.responseStatusRequestMaker(
      "/agent/join/$address",
      (r:WSRequest) => (if(wan) r.withQueryString(("wan","1")) else r).get()
    )( _ == Status.OK )

    def `force-leave`(node: Types.NodeId): Future[Boolean] = rb.responseStatusRequestMaker(
      s"/agent/force-leave/$node",_.get()
    )( _ == Status.OK )

    def maintenance(enable:Boolean,reason:Option[String]): Future[Boolean] = {
      lazy val params = Seq(("enable",enable.toString)) ++ reason.map("reason"->_)
      rb.responseStatusRequestMaker("/agent/maintenance", _.withQueryString(params:_*).put(JsNull) )(_ == Status.OK)
    }

    def checks(): Future[Map[CheckId, Check]] = rb.erased(
      rb.jsonRequestMaker("/agent/checks", _.get() )(
        _.validate[Map[String,Check]].map(_.map{ case (key,value) => CheckId(key)->value })
      )
    )

    def services(): Future[Map[ServiceId,Service]] = rb.erased(
      rb.jsonRequestMaker("/agent/services", _.get() )(
        _.validate[Map[String,Service]].map(_.map{ case (key,value) => ServiceId(key)->value })
      )
    )

    lazy val service: ServiceRequests = ServiceRequests()
    lazy val check: CheckRequests = CheckRequests()

  }
}
