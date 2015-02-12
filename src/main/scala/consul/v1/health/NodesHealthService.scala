package consul.v1.health

import java.net.InetSocketAddress

import consul.v1.common.{Service, CheckStatus, Node}

case class NodesHealthService(Node: Node, Service: Service, Checks: Seq[Check]){

  lazy val status = Checks.collectFirst{ case check if check.ServiceID == Service.ID => check.Status }.getOrElse(CheckStatus.unknown)

  lazy val address = new InetSocketAddress(Node.Address,Service.Port)
}
