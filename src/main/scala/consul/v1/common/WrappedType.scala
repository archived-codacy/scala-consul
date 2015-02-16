package consul.v1.common

case class WrappedType[A,B](value:A){
  override def toString = value.toString
}
object WrappedType{
  import play.api.libs.json._
  import scala.language.implicitConversions

  implicit def writes[A,B](implicit aWrites: Writes[A]):Writes[WrappedType[A,B]] =
    Writes( (id:WrappedType[A,B]) => aWrites.writes(id.value) )

  implicit def reads[A,B](implicit aReads: Reads[A]):Reads[WrappedType[A,B]] =
    aReads.map( WrappedType[A,B](_:A) )

  implicit def unboxed[A,B](identifier: WrappedType[A,B]): A = identifier.value
}
