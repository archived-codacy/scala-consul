package consul.v1.event

sealed trait EventIds

case class Event(ID:EventId,Name:String,Payload:Option[String],NodeFilter:String,ServiceFilter:String,TagFilter:String,Version:Double,LTime:Double)
