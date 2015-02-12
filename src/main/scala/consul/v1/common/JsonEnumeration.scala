package consul.v1.common

import play.api.data.validation.ValidationError
import play.api.libs.json.{Format, Reads, Json, Writes}

object Implicits {
  import scala.language.implicitConversions
  implicit def enumWrites[E <: Enumeration#Value]: Writes[E] = Writes( (e:E) => Json.toJson(e.toString))

  implicit def enumReads[E <: Enumeration](e:E): Reads[e.Value] = {
    Reads.StringReads.map{ case value =>  e.values.find(_.toString == value) }.
      collect(ValidationError("Invalid enumeration value")){ case Some(v) => v }
  }
}

trait JsonEnumeration{ self: Enumeration =>

  implicit lazy val reads = Implicits.enumReads(self)
  implicit val format = Format(reads,Implicits.enumWrites)

}