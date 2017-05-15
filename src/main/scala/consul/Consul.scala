package consul

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import consul.v1.acl.AclRequests
import consul.v1.agent.AgentRequests
import consul.v1.catalog.CatalogRequests
import consul.v1.common.{ConsulRequestBasics, Types}
import consul.v1.event.EventRequests
import consul.v1.health.HealthRequests
import consul.v1.kv.KvRequests
import consul.v1.session.SessionRequests
import consul.v1.status.StatusRequests
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import play.api.Application
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.{WS, WSClient}

import scala.concurrent.ExecutionContext

trait ConsulApiV1{
  def acl:     AclRequests
  def agent:   AgentRequests
  def catalog: CatalogRequests
  def event:   EventRequests
  def health:  HealthRequests
  def kv:      KvRequests
  def session: SessionRequests
  def status:  StatusRequests

}

class Consul(address: InetAddress, port: Int = 8500, token: Option[String] = None, client: WSClient)
            (implicit executionContext: ExecutionContext){

  lazy val v1: ConsulApiV1 with Types = new ConsulApiV1 with Types{
    private implicit def requestBasics = new ConsulRequestBasics(token, client)
    private lazy val basePath = s"http://${address.getHostName}:$port/v1"
    lazy val health:  HealthRequests  = HealthRequests( basePath)
    lazy val agent:   AgentRequests   = AgentRequests(  basePath)
    lazy val catalog: CatalogRequests = CatalogRequests(basePath)
    lazy val kv:      KvRequests      = KvRequests(     basePath)
    lazy val status:  StatusRequests  = StatusRequests( basePath)
    lazy val acl:     AclRequests     = AclRequests(    basePath)
    lazy val session: SessionRequests = SessionRequests(basePath)
    lazy val event:   EventRequests   = EventRequests(  basePath)
  }

}

object Consul {
  def inApplication(address: InetAddress, port: Int = 8500, token: Option[String] = None)
                   (implicit executionContext: ExecutionContext, app: Application): Consul =
    new Consul(address, port, token, WS.client)

  def standalone(address: InetAddress, port: Int = 8500, token: Option[String] = None)
                (implicit executionContext: ExecutionContext): Consul = {
    implicit val system = ActorSystem("ahcSystem", defaultExecutionContext = Some(executionContext))
    implicit val materializer = ActorMaterializer()
    val builder = new DefaultAsyncHttpClientConfig.Builder()
    val client = new AhcWSClient(builder.build())
    new Consul(address, port, token, client)
  }
}
