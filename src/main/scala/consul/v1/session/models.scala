package consul.v1.session

import consul.v1.common.JsonEnumeration
import consul.v1.common.Types._

sealed trait SessionIds

object Behaviour extends Enumeration with JsonEnumeration{
  val release,delete = Value
}

case class SessionDef(LockDelay:Option[String], Name:Option[String], Node:Option[String],
                      Checks:Option[Seq[CheckId]], Behavior:Option[Behaviour.Value], TTL:Option[String])
case class SessionInfo(LockDelay:Double,Checks:Seq[CheckId],Node:NodeId,ID:SessionId,CreateIndex:Long)
case class SessionIDHolder(ID:SessionId)
