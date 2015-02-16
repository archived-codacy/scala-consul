package consul.v1.agent.check

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types
import consul.v1.common.Types.CheckId
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.WSRequestHolder

import scala.concurrent.{ExecutionContext, Future}

case class Check(ID:CheckId,Name:String,Notes:Option[String],Script:Option[String],Interval:Option[String],TTL:Option[String])


object Check{
  implicit lazy val writes = ???

  def apply(Name:String,Notes:Option[String],Script:Option[String],Interval:Option[String],TTL:Option[String]):Check =
    Check(CheckId(Name),Name,Notes,Script,Interval,TTL)

}

trait CheckRequests{
  def register(check:Check):Future[Boolean]
  def deregister(checkId:CheckId):Future[Boolean]
  def pass(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]
  def warn(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]
  def fail(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]

}

object CheckRequests{

  def apply(basePath: String)(implicit executionContext: ExecutionContext): CheckRequests = new CheckRequests {

    def register(check: Check): Future[Boolean] = responseStatusRequestMaker(
      registerPath,_.put(Json.toJson(check))
    )(_ == Status.OK)

    def deregister(checkId: Types.CheckId): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(s"deregister/$checkId"),_.get()
    )(_ == Status.OK)

    def pass(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("pass")(checkId,note)

    def warn(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("warn")(checkId,note)

    def fail(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("fail")(checkId,note)

    private def functionForStatus(status:String) = (checkId: CheckId,note:Option[String]) => {
      val httpFunc =
        (r:WSRequestHolder) => note.map{ case note => r.withQueryString("note"->note) }.getOrElse( r ).get()

      responseStatusRequestMaker(fullPathFor(s"$status/$checkId"),httpFunc)(_ == Status.OK)
    }

    private def fullPathFor(path: String) = s"$basePath/check/$path"

    private lazy val registerPath = fullPathFor("register")
  }
}