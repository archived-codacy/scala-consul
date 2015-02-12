package consul.v1.agent.service

case class Check(Script: Option[String], Interval: Option[String], TTL: Option[String])