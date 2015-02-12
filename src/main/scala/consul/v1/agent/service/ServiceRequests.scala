package consul.v1.agent.service

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types.ServiceId
import play.api.http.Status
import play.api.libs.json.{Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

trait ServiceRequests {
  def register(localService: LocalService): Future[Boolean]

  def deregister(serviceID:ServiceId):Future[Boolean]

  def LocalService = consul.v1.agent.service.LocalService.apply _

  def ttlCheck(ttl: String): Check = Check(Option.empty, Option.empty, Option(ttl))

  def scriptCheck(script: String, interval: String): Check = Check(Option(script), Option(interval), Option.empty)
}

object ServiceRequests {

  private implicit lazy val CheckWrites: Writes[Check] = Json.writes[Check]
  private implicit lazy val LocalServiceWrites: Writes[LocalService] = Json.writes[LocalService]

  def apply(basePath: String)(implicit executionContext: ExecutionContext): ServiceRequests = new ServiceRequests {

    def register(localService: LocalService): Future[Boolean] =
      responseStatusRequestMaker(fullPathFor("register"), _.put(Json.toJson(localService)))(_ == Status.OK)

    def deregister(serviceID: ServiceId): Future[Boolean] =
      responseStatusRequestMaker(fullPathFor(s"deregister/$serviceID"), _.get())(_ == Status.OK)

    private def fullPathFor(path: String) = s"$basePath/service/$path"

  }

}