package consul.v1.common

import consul.v1.common.Types.NodeId
import play.api.libs.functional.syntax._
import play.api.libs.json._
case class Node(Node: NodeId, Address: String)

object Node {

  implicit lazy val fmt: OFormat[Node] = (
      (__ \ "Node"   ).format[NodeId] and
      (__ \ "Address").format[String]
    )(Node.apply _, unlift(Node.unapply _))
}