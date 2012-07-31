package wtfcode.model

import net.liftweb.mapper._
import net.liftweb.http.S
import net.liftweb.common.Full

class User extends MegaProtoUser[User] with CreatedTrait with OneToMany[Long, User] {
  def getSingleton = User

  object nickName extends MappedString(this, 16) {
    override def dbIndexed_? = true
    override def validations = valUnique(S ? "user.uniqueNickname") _ :: super.validations
    override def displayName = S ? "user.nickname"
  }
  object aboutMe extends MappedTextarea(this, 1024) {
    override def displayName = S ? "user.aboutMe"
    override def textareaRows = 10
    override def textareaCols = 80
  }
  object bookmarks extends MappedOneToMany(Bookmark, Bookmark.user, OrderBy(Bookmark.createdAt, Descending))

  def link = "/user/" + nickName
}

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  override def screenWrap = Full(<lift:surround with="default" at="content">
    <lift:bind/></lift:surround>)

  override val skipEmailValidation = true //FIXME

  override def signupFields = List(nickName, email, password)

  override def editFields = List(email, locale, timezone, aboutMe)
}