package consul.v1.common

import play.api.libs.json._
import play.api.libs.ws.{WS, WSRequestHolder, WSResponse}
import play.api.Play.current
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ConsulRequestBasics {

  def jsonRequestMaker[A](path: String, httpFunc: WSRequestHolder => Future[WSResponse])(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    val req = WS.url(path)
    httpFunc(req).map { case res =>
      val json = Try(res.json).getOrElse(JsNull)
      body(json)
    }
  }

  def jsonDcRequestMaker[A](path: String, dc:Option[String], httpFunc: WSRequestHolder => Future[WSResponse])(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    jsonRequestMaker(
      path,
      (req:WSRequestHolder) => httpFunc(dc.map{ case dc => req.withQueryString("dc"->dc) }.getOrElse(req))
    )(body)
  }

  def responseStatusRequestMaker[A](path: String, httpFunc: WSRequestHolder => Future[WSResponse])(body: Int => A)(implicit executionContext: ExecutionContext): Future[A] = {
    val req = WS.url(path)
    httpFunc(req).map { case res =>
      body(res.status)
    }
  }

  def stringRequestMaker[A](path: String, httpFunc: WSRequestHolder => Future[WSResponse])(body: String => A)(implicit executionContext: ExecutionContext): Future[A] = {
    val req = WS.url(path)
    httpFunc(req).map { case res =>
      body(res.body)
    }
  }

  def erased[A](future:Future[JsResult[A]])(implicit executionContext: ExecutionContext) = {
    future.flatMap(_ match{
      case err:JsError      => Future.failed(Types.ConsulResponseParseException(err))
      case JsSuccess(res,_) => Future.successful(res)
    })
  }

}
