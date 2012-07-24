package badcode.model

import net.liftweb.mapper.{CreatedTrait, MappedString, MetaMegaProtoUser, MegaProtoUser}
import net.liftweb.common.Full
import net.liftweb.http.S

class User extends MegaProtoUser[User] with CreatedTrait {
  def getSingleton = User

  object nickName extends MappedString(this, 16) {
    override def dbIndexed_? = true
    override def displayName = S.??("Nickname")
  }
}

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  override def screenWrap = Full(<lift:surround with="default" at="content">
    <lift:bind/></lift:surround>)

  override val skipEmailValidation = true //FIXME

  override def signupFields = List(nickName, email, password)
}