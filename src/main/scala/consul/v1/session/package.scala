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

  case class SessionDef(LockDelay:Option[String]=Option.empty,Name:Option[String]=Option.empty,Node:Option[String]=Option.empty,Checks:Option[Seq[CheckId]]=Option.empty,Behavior:Option[Behaviour.Value]=Option.empty,TTL:Option[String]=Option.empty)
  case class SessionInfo(LockDelay:Double,Checks:Seq[CheckId],Node:NodeId,ID:SessionId,CreateIndex:Long)
  case class SessionIDHolder(ID:SessionId)
}
