package wtfcode.model

import net.liftweb.mapper._
import net.liftweb.http.S
import net.liftweb.common.Full

class User extends MegaProtoUser[User] with CreatedTrait {
  def getSingleton = User

  object nickName extends MappedString(this, 16) {
    override def dbIndexed_? = true
    override def validations = valUnique(S ? "Nickname must be unique") _ :: super.validations
    override def displayName = S ? "Nickname"
  }

  def link: String = "/user/" + nickName
}

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  override def screenWrap = Full(<lift:surround with="default" at="content">
    <lift:bind/></lift:surround>)

  override val skipEmailValidation = true //FIXME

  override def signupFields = List(nickName, email, password)
}