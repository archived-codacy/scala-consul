package consul.v1.kv

case class KvValue(CreateIndex: Int, ModifyIndex: Int, LockIndex: Int, Key: String, Flags: Int, Value: String, Session: String)