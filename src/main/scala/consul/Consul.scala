package consul

import java.net.Inet4Address

import consul.v1.agent.AgentRequests
import consul.v1.catalog.CatalogRequests
import consul.v1.health.HealthRequests
import consul.v1.kv.KvRequests
import consul.v1.status.StatusRequests

import scala.concurrent.ExecutionContext

trait ConsulApiV1{
  def health:  HealthRequests
  def agent:   AgentRequests
  def catalog: CatalogRequests
  def kv:      KvRequests
  def status:  StatusRequests
}

class Consul(address: Inet4Address, port: Int = 8500)(implicit executionContext: ExecutionContext){

  lazy val v1: ConsulApiV1 = new ConsulApiV1{
    private lazy val basePath = s"http://${address.getHostAddress}:$port/v1"
    lazy val health:  HealthRequests  = HealthRequests(basePath)
    lazy val agent:   AgentRequests   = AgentRequests(basePath)
    lazy val catalog: CatalogRequests = CatalogRequests(basePath)
    lazy val kv: KvRequests           = KvRequests(basePath)
    lazy val status:StatusRequests    = StatusRequests(basePath)
  }

}
