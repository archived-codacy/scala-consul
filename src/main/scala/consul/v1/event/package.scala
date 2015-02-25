package consul.v1

import consul.v1.common.WrappedType

package object event {

  sealed trait EventIds
  type EventId = WrappedType[String,EventIds]
  def EventId:String => EventId = WrappedType.apply

  case class Event(ID:EventId,Name:String,Payload:Option[String],NodeFilter:String,ServiceFilter:String,TagFilter:String,Version:Double,LTime:Double)
}
