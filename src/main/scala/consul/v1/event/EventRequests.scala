package consul.v1.event

import consul.v1.common.ConsulRequestBasics._
import consul.v1.common.Types._
import play.api.http.{ContentTypeOf, Writeable}
import play.api.libs.json.Json
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}

trait EventRequests {

  def fire[T](name:String, payload:T,node:Option[NodeId]=Option.empty,service:Option[ServiceId]=Option.empty, tag:Option[ServiceTag]=Option.empty,dc:Option[DatacenterId]=Option.empty)(implicit wrt: Writeable[T], ct: ContentTypeOf[T]):Future[Event]
  def list(name:Option[String]=Option.empty):Future[List[Event]]

  def EventId: (String) => EventId = consul.v1.event.EventId
}

object EventRequests{

  private implicit lazy val eventReads = Json.reads[Event]

  def apply(basePath: String)(implicit executionContext: ExecutionContext):EventRequests = new EventRequests {

    def fire[T](name:String, payload:T,node:Option[NodeId],service:Option[ServiceId],tag:Option[ServiceTag],dc:Option[DatacenterId])(
                implicit wrt: Writeable[T], ct: ContentTypeOf[T]):Future[Event] = {

      val params = node.map("node"->_.toString).toList ++ service.map("service"->_.toString) ++ tag.map("tag"->_.toString)
      erased(
        jsonDcRequestMaker(fullPathFor(s"fire/$name"),dc,
          _.withQueryString(params:_*).put(payload)
        )(_.validate[Event])
      )
    }

    def list(name: Option[String]): Future[List[Event]] = erased(
      jsonRequestMaker(listPath,
        (r:WSRequest) => name.map{ case name => r.withQueryString("name"->name) }.getOrElse(r).get()
      )(_.validate[List[Event]])
    )

    private lazy val listPath = fullPathFor("list")
    private def fullPathFor(path: String) = s"$basePath/event/$path"
  }
}
