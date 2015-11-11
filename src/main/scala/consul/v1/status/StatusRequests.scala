package consul.v1.status

import consul.v1.common.ConsulRequestBasics
import scala.concurrent.{ExecutionContext, Future}

trait StatusRequests {
  def leader():Future[Option[String]]
  def peers(): Future[Seq[String]]
}
object StatusRequests{

  def apply()(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): StatusRequests = new StatusRequests{

    def leader(): Future[Option[String]] = rb.erased(
      rb.jsonRequestMaker("/status/leader",_.get())(_.validateOpt[String])
    )

    def peers(): Future[Seq[String]] = rb.erased(
      rb.jsonRequestMaker("/status/peers",_.get())(_.validate[Seq[String]])
    )

  }

}