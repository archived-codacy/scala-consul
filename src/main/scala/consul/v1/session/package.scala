package consul.v1

import consul.v1.common.Types._
import consul.v1.common.{JsonEnumeration, WrappedType}

package object session {

  sealed trait SessionIds

  type SessionId = WrappedType[String,SessionIds]
  def SessionId:String => SessionId = WrappedType.apply

  object Behaviour extends Enumeration with JsonEnumeration{
    val release,delete = Value
  }

  case class SessionDef(LockDelay:Option[String], Name:Option[String], Node:Option[String],
                        Checks:Option[Seq[CheckId]], Behavior:Option[Behaviour.Value], TTL:Option[String])
  case class SessionInfo(LockDelay:Double,Checks:Seq[CheckId],Node:NodeId,ID:SessionId,CreateIndex:Long)
  case class SessionIDHolder(ID:SessionId)
}
