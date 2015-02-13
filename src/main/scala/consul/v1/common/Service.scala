package consul.v1.common

import consul.v1.common.Types.{ServiceType, ServiceId}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Service(ID: ServiceId, Service: ServiceType, Tags: Set[String], Port: Int)

object Service {

  implicit lazy val fmt: OFormat[Service] = (
      (__ \ "ID"     ).format[ServiceId] and
      (__ \ "Service").format[ServiceType] and
      (__ \ "Tags"   ).formatNullable[Set[String]].inmap[Set[String]](_.getOrElse(Set.empty),Option(_)) and
      (__ \ "Port"   ).format[Int]
    )(Service.apply _, unlift(Service.unapply _))
}
