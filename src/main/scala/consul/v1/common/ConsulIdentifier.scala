package consul.v1.common

import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json.{Format, Json, Writes}

import scala.reflect.ClassTag

case class ConsulIdentifier[T](value:String){
  override def toString = value
}

object ConsulIdentifier{

  implicit def fmt[A](implicit tag:ClassTag[A]): Format[ConsulIdentifier[A]] = Format(
    StringReads.map{ case value => ConsulIdentifier[A](value) },
    Writes((a:ConsulIdentifier[A]) => Json.toJson(a.value))
  )
  import scala.language.implicitConversions
  implicit def stringMe[A](identifier: ConsulIdentifier[A]) = identifier.value
}