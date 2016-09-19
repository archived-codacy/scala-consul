package consul

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.stream.Materializer

import consul.v1.acl.AclRequests
import consul.v1.agent.AgentRequests
import consul.v1.catalog.CatalogRequests
import consul.v1.common.{ConsulRequestBasics, Types}
import consul.v1.event.EventRequests
import consul.v1.health.HealthRequests
import consul.v1.kv.KvRequests
import consul.v1.session.SessionRequests
import consul.v1.status.StatusRequests
import play.api.Application
import play.api.libs.ws.{ WS, WSClient }
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.ahc.AhcConfigBuilder
import org.asynchttpclient.AsyncHttpClientConfig
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder

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
    private lazy val basePath = s"http://${address.getHostAddress}:$port/v1"
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
  // In Play 2.5.X libs, the built-in WS object is deprecated, so you should now create your own.
  //
  // The notion of standalone and application are also deprecated, now it is just default and non-default configurations

  def usingClient(client: AhcWSClient, address: InetAddress, port: Int = 8500, token: Option[String] = None)(implicit executionContext: ExecutionContext, materializer: Materializer): Consul = {
    new Consul(address, port, token, client)
  }

  def fromConfig(config: AsyncHttpClientConfig, address: InetAddress, port: Int = 8500, token: Option[String] = None)(implicit executionContext: ExecutionContext, materializer: Materializer): Consul = {
    usingClient(new AhcWSClient(config), address, port, token)
  }

  def withDefaultConfig(address: InetAddress, port: Int = 8500, token: Option[String] = None)(implicit executionContext: ExecutionContext, materializer: Materializer): Consul = {
    fromConfig(new Builder().build(), address, port, token)
  }

}
