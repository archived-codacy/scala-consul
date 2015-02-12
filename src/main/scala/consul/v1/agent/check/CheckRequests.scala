package consul.v1.agent.check

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types.CheckId
import play.api.http.Status
import play.api.libs.ws.WSRequestHolder

import scala.concurrent.{ExecutionContext, Future}
trait CheckRequests{
  //def register
  //def deregister
  def pass(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]
  def warn(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]
  def fail(checkId:CheckId,note:Option[String]=Option.empty):Future[Boolean]
}

object CheckRequests{

  def apply(basePath: String)(implicit executionContext: ExecutionContext): CheckRequests = new CheckRequests {

    def pass(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("pass")(checkId,note)

    def warn(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("warn")(checkId,note)

    def fail(checkId: CheckId,note:Option[String]): Future[Boolean] = functionForStatus("fail")(checkId,note)

    private def functionForStatus(status:String) = (checkId: CheckId,note:Option[String]) => {
      val httpFunc =
        (r:WSRequestHolder) => note.map{ case note => r.withQueryString("note"->note) }.getOrElse( r ).get()

      responseStatusRequestMaker(fullPathFor(s"$status/$checkId"),httpFunc)(_ == Status.OK)
    }

    private def fullPathFor(path: String) = s"$basePath/check/$path"
  }
}