package consul.v1.agent.check

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types.CheckId
import play.api.http.Status
import play.api.libs.json.{Writes, Json}
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}

case class Check(ID:CheckId,Name:String,Notes:Option[String],Script:Option[String],HTTP:Option[String],Interval:Option[String],TTL:Option[String])

object Check{
  implicit lazy val writes: Writes[Check] = Json.writes[Check]
}

trait CheckCreators{
  def ttlCheck(ID:CheckId,Name:String,Notes:Option[String],TTL:String):Check =
    Check(ID,Name,Notes,Option.empty,Option.empty,Option.empty,Option(TTL))

  def ttlCheck(Name:String,Notes:Option[String],TTL:String):Check =
    ttlCheck(CheckId(Name),Name,Notes,TTL)

  def scriptCheck(ID:CheckId,Name:String,Notes:Option[String],Script:String,Interval:String):Check =
    Check(ID,Name,Notes,Option(Script),Option.empty,Option(Interval),Option.empty)

  def scriptCheck(Name:String,Notes:Option[String],Script:String,Interval:String):Check =
    scriptCheck(CheckId(Name),Name,Notes,Script,Interval)

  def httpCheck(ID:CheckId,Name:String,Notes:Option[String],HTTP:String,Interval:String):Check =
    Check(ID,Name,Notes,Option.empty,Option(HTTP),Option(Interval),Option.empty)

  def httpCheck(Name:String,Notes:Option[String],Script:String,Interval:String):Check =
    httpCheck(CheckId(Name),Name,Notes,Script,Interval)

}

trait CheckRequests extends CheckCreators{
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

    def deregister(checkId: CheckId): Future[Boolean] = responseStatusRequestMaker(
      fullPathFor(s"deregister/$checkId"),_.get()
    )(_ == Status.OK)

    def pass(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("pass")(checkId,note)

    def warn(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("warn")(checkId,note)

    def fail(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("fail")(checkId,note)

    private def functionForStatus(status:String) = (checkId: CheckId,note:Option[String]) =>
      responseStatusRequestMaker(
        fullPathFor(s"$status/$checkId"),
        (r:WSRequest) => note.map{ case note => r.withQueryString("note"->note) }.getOrElse( r ).get()
      )(_ == Status.OK)

    private def fullPathFor(path: String) = s"$basePath/check/$path"

    private lazy val registerPath = fullPathFor("register")
  }
}