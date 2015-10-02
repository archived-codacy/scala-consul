package consul.v1.status

import consul.v1.common.ConsulRequestBasics._
import scala.concurrent.{ExecutionContext, Future}

trait StatusRequests {
  def leader():Future[Option[String]]
  def peers(): Future[Seq[String]]
}
object StatusRequests{

  def apply(basePath: String)(implicit executionContext: ExecutionContext): StatusRequests = new StatusRequests{

    def leader(): Future[Option[String]] = erased(
      jsonRequestMaker(fullPathFor("leader"),_.get())(_.validateOpt[String])
    )

    def peers(): Future[Seq[String]] = erased(
      jsonRequestMaker(fullPathFor("peers"),_.get())(_.validate[Seq[String]])
    )

    private def fullPathFor(path: String) = s"$basePath/status/$path"
  }

}