package consul.v1.common

object CheckStatus extends Enumeration with JsonEnumeration{
  type CheckStatus = Value
  val passing, unknown, warning, critical = Value
}
