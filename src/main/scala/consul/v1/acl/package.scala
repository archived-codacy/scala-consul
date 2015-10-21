package consul.v1

import consul.v1.common.WrappedType

package object acl {
  type AclId = WrappedType[String,AclIds]
  def AclId: String => AclId = WrappedType.apply
}
