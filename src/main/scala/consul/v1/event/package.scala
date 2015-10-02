package consul.v1

import consul.v1.common.WrappedType

package object event {
  type EventId = WrappedType[String,EventIds]
  def EventId:String => EventId = WrappedType.apply
}
