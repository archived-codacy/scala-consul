package consul.v1.common

object CheckStatus extends Enumeration with JsonEnumeration{
  type CheckStatus = Value
  val passing, warning, critical, unknown, any = Value
}
