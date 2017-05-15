package consul.v1.kv

import java.util.Base64
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class KvValue(CreateIndex: Int, ModifyIndex: Int, LockIndex: Int, Key: String, Flags: Int, Value: Option[String], Session: Option[String])
object KvValue{

  val base64valueReads = StringReads.map{ case encodedValue =>
    new String(Base64.getDecoder.decode(encodedValue))
  }

  implicit val reads = (
    (__ \ "CreateIndex").read[Int] and
    (__ \ "ModifyIndex").read[Int] and
    (__ \ "LockIndex"  ).read[Int] and
    (__ \ "Key"        ).read[String] and
    (__ \ "Flags"      ).read[Int] and
    (__ \ "Value"      ).readNullable(base64valueReads) and
    (__ \ "Session"    ).readNullable[String]
  )(KvValue.apply _)
}