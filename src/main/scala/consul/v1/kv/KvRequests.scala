package consul.v1.kv

import consul.v1.common.ConsulRequestBasics
import consul.v1.common.Types._
import play.api.http.{ContentTypeOf, Writeable}
import play.api.libs.ws.WSRequest

import scala.concurrent.{ExecutionContext, Future}
trait KvRequests {

  def get(key:String,recurse:Boolean=false,dc:Option[DatacenterId]=Option.empty): Future[List[KvValue]]
  //def getRaw(key:String): Future[Option[String]]
  def put[T](key: String, value: T, flags: Option[Int]=Option.empty, acquire: Option[Int]=Option.empty, release: Option[String]=Option.empty,dc:Option[DatacenterId]=Option.empty)(implicit wrt: Writeable[T], ct: ContentTypeOf[T]):Future[Boolean]
  def delete(key:String,recurse:Boolean,dc:Option[DatacenterId]=Option.empty):Future[_]
}

object KvRequests {

  def apply(basePath: String)(implicit executionContext: ExecutionContext, rb: ConsulRequestBasics): KvRequests = new KvRequests{

    def get(key: String,recurse:Boolean,dc:Option[DatacenterId]) = rb.erased(
      rb.jsonRequestMaker(
        fullPathFor(key),
        httpFunc = recurseDcRequestHolder(recurse,dc).andThen( _.get() )
      )(_.validateOpt[List[KvValue]].map(_.getOrElse(List.empty)))
    )

    def put[T](key: String, value: T, flags: Option[Int], acquire: Option[Int], release: Option[String], dc:Option[DatacenterId])(implicit wrt: Writeable[T], ct: ContentTypeOf[T]): Future[Boolean] = {
      //this could be wrong - could be a simple string that is returned and we have to check if "true" or "false"
      val params = flags.map("flags"->_.toString).toList ++ acquire.map("acquire"->_.toString) ++ release.map("release"->_)

      rb.jsonDcRequestMaker(
        fullPathFor(key),dc,
        httpFunc = _.withQueryString(params:_*).put(value)
      )(_.validate[Boolean].getOrElse(false))
    }

    def delete(key: String, recurse: Boolean, dc:Option[DatacenterId]): Future[Boolean] = {
      rb.responseStatusRequestMaker(fullPathFor(key),
       httpFunc = recurseDcRequestHolder(recurse,dc).andThen( _.delete() )
      )(_ => true)
    }

    private def recurseDcRequestHolder(recurse:Boolean,dc:Option[DatacenterId]) = {
      val params = dc.map("dc"->_.toString).toList ++ Option(recurse).collect{ case true => "recurse" -> "true" }
      (_:WSRequest).withQueryString(params:_*)
    }

    private def fullPathFor(key: String) = s"$basePath/kv/$key"
  }


}