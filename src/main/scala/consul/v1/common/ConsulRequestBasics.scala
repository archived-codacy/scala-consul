package consul.v1.common

import consul.v1.common.Types.DatacenterId
import play.api.libs.json._
import play.api.libs.ws.{WS, WSRequest, WSResponse}
import play.api.Play.current
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ConsulRequestBasics {

  def jsonRequestMaker[A](path: String, httpFunc: WSRequest => Future[WSResponse])(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.json)(body)
  }

  def jsonDcRequestMaker[A](path: String, dc:Option[DatacenterId], httpFunc: WSRequest => Future[WSResponse])(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    jsonRequestMaker(path, withDc(httpFunc,dc))(body)
  }

  def responseStatusRequestMaker[A](path: String, httpFunc: WSRequest => Future[WSResponse])(body: Int => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.status)(body)
  }

  def responseStatusDcRequestMaker[A](path: String, dc:Option[DatacenterId], httpFunc: WSRequest => Future[WSResponse])(body: Int => A)(implicit executionContext: ExecutionContext): Future[A] = {
    responseStatusRequestMaker(path, withDc(httpFunc,dc))(body)
  }

  def stringRequestMaker[A](path: String, httpFunc: WSRequest => Future[WSResponse])(body: String => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.body)(body)
  }

  def erased[A](future:Future[JsResult[A]])(implicit executionContext: ExecutionContext): Future[A] = {
    future.flatMap {
      case err: JsError => Future.failed(Types.ConsulResponseParseException(err))
      case JsSuccess(res, _) => Future.successful(res)
    }
  }

  private def genRequestMaker[A,B](path: String, httpFunc: WSRequest => Future[WSResponse])(responseTransformer: WSResponse => B)(body: B => A)(implicit executionContext: ExecutionContext): Future[A] = {

    Try(httpFunc(WS.url(path))) match {
      case Failure(exception) => Future.failed(exception)
      case Success(resultF)   => resultF.map( responseTransformer andThen body )
    }
  }

  private def withDc(httpFunc: WSRequest => Future[WSResponse],dc:Option[DatacenterId]) = {
    (req:WSRequest) => httpFunc(dc.map{ v => req.withQueryString("dc" -> v.toString) }.getOrElse(req))
  }
}