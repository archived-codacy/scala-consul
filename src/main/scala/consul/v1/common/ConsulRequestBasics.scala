package consul.v1.common

import consul.v1.common.Types.DatacenterId
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ConsulRequestBasics(token: Option[String], client: WSClient) {
  // you must provide a WSClient and manage its lifecycle

  type HttpFunc = WSRequest => Future[WSResponse]
  type RequestTransformer = WSRequest => WSRequest

  def jsonRequestMaker[A](path: String, httpFunc: HttpFunc)(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.json)(body)
  }

  def jsonDcRequestMaker[A](path: String, dc:Option[DatacenterId], httpFunc: HttpFunc)(body: JsValue => A)(implicit executionContext: ExecutionContext): Future[A] = {
    jsonRequestMaker(path, withDc(dc).andThen(httpFunc))(body)
  }

  def responseStatusRequestMaker[A](path: String, httpFunc: HttpFunc)(body: Int => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.status)(body)
  }

  def responseStatusDcRequestMaker[A](path: String, dc:Option[DatacenterId], httpFunc: HttpFunc)(body: Int => A)(implicit executionContext: ExecutionContext): Future[A] = {
    responseStatusRequestMaker(path, withDc(dc).andThen(httpFunc))(body)
  }

  def stringRequestMaker[A](path: String, httpFunc: HttpFunc)(body: String => A)(implicit executionContext: ExecutionContext): Future[A] = {
    genRequestMaker(path,httpFunc)(_.body)(body)
  }

  def erased[A](future:Future[JsResult[A]])(implicit executionContext: ExecutionContext): Future[A] = {
    future.flatMap {
      case err: JsError => Future.failed(Types.ConsulResponseParseException(err))
      case JsSuccess(res, _) => Future.successful(res)
    }
  }

  private def genRequestMaker[A,B](path: String, httpFunc: HttpFunc)(responseTransformer: WSResponse => B)(body: B => A)(implicit executionContext: ExecutionContext): Future[A] = {
    Try((withToken(token) andThen httpFunc)(client.url(path))) match {
      case Failure(exception) => Future.failed(exception)
      case Success(resultF)   => resultF.map( responseTransformer andThen body )
    }
  }

  private def withToken(token: Option[String]): RequestTransformer = {
    token.map(t => (req: WSRequest) => req.withQueryString("token" -> t)).getOrElse(identity)
  }

  private def withDc(dc: Option[DatacenterId]): RequestTransformer = {
    dc.map(v => (req: WSRequest) => req.withQueryString("dc" -> v.toString)).getOrElse(identity)
  }
}
