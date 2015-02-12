package consul.v1.agent.service

import consul.v1.common.Types.{CheckId, ServiceId}

case class LocalService(ID: ServiceId, Name: String, Tags: Set[String] = Set.empty, Port: Option[Int], Check: Option[Check]){

  lazy val checkId:CheckId = CheckId(s"service:$ID")

}

//naming this factory so it doesn't interfere with Json.format macro....
object LocalServiceFactory {
  //no id is provided -> id becomes name
  def apply(Name: String, Tags: Set[String], Port: Option[Int], Check: Option[Check]) =
    LocalService(ServiceId(Name), Name, Tags, Port, Check)
}
