package badcode.model

import net.liftweb.mapper.{MetaMegaProtoUser, MegaProtoUser}
import net.liftweb.common.Full

class User extends MegaProtoUser[User] {
  def getSingleton = User
}

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  override def screenWrap = Full(<lift:surround with="default" at="content">
    <lift:bind/></lift:surround>)

  override val skipEmailValidation = true //FIXME
}