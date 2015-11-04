package consul.v1.agent.service

import consul.v1.agent.service.LocalService.{apply => applied}
import consul.v1.common.ConsulRequestBasics
import consul.v1.common.Types._
import play.api.http.Status
import play.api.libs.json.{JsNull, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

trait ServiceRequests {
  def register(localService: LocalService): Future[Boolean]

  def deregister(serviceID:ServiceId):Future[Boolean]

  def maintenance(serviceID:ServiceId,enable:Boolean,reason:Option[String]):Future[Boolean]

  def LocalService = applied(_: ServiceId, _: ServiceType, _: Set[ServiceTag], _: Option[Int], _: Option[Check])

  def ttlCheck(ttl: String): Check = Check(Option.empty, Option.empty,Option.empty, Option(ttl))

  def scriptCheck(script: String, interval: String): Check = Check(Option(script), Option.empty,Option(interval), Option.empty)

  def httpCheck(http:String,interval:String):Check = Check(Option.empty, Option(http), Option(interval), Option.empty)
}

object ServiceRequests {

  private implicit lazy val CheckWrites: Writes[Check] = Json.writes[Check]

  def apply(basePath: String)(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): ServiceRequests = new ServiceRequests{

    import rb._

    def maintenance(serviceID: ServiceId,enable:Boolean,reason:Option[String]): Future[Boolean] = {
      lazy val params = Seq(("enable",enable.toString)) ++ reason.map("reason"->_)
      responseStatusRequestMaker(
        fullPathFor(s"maintenance/$serviceID"),
        _.withQueryString(params:_*).put(JsNull)
      )(_ == Status.OK)
    }

    def register(localService: LocalService): Future[Boolean] =
      responseStatusRequestMaker(fullPathFor("register"), _.put(Json.toJson(localService)))(_ == Status.OK)

    def deregister(serviceID: ServiceId): Future[Boolean] =
      responseStatusRequestMaker(fullPathFor(s"deregister/$serviceID"), _.get())(_ == Status.OK)

    private def fullPathFor(path: String) = s"$basePath/service/$path"

  }

}