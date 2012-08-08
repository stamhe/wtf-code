package wtfcode.model

import net.liftweb.mapper._
import net.liftweb.http.S
import net.liftweb.common.Full
import net.liftweb.util.FieldError
import xml.Text

class User extends MegaProtoUser[User] with CreatedTrait with OneToMany[Long, User] {
  def getSingleton = User

  object nickName extends MappedString(this, 16) {
    def reserved(value: String): List[FieldError] = {
      value match {
        case "Guest" => List(FieldError(this, Text(S ? "user.nicknameReserved")))
        case _ => Nil
      }
    }

    def allowedChars(value: String): List[FieldError] = {
      value.matches("\\p{Alnum}[\\p{Alnum}-]+\\p{Alnum}") match {
        case true => Nil
        case false => List(FieldError(this, Text(S ? "user.nicknameBadChars")))
      }
    }

    override def dbIndexed_? = true
    override def validations = valUnique(S ? "user.uniqueNickname") _ :: reserved _ :: allowedChars _ :: super.validations
    override def displayName = S ? "user.nickname"
  }
  object aboutMe extends MappedTextarea(this, 1024) {
    override def displayName = S ? "user.aboutMe"
    override def textareaRows = 10
    override def textareaCols = 80
  }
  object bookmarks extends MappedOneToMany(Bookmark, Bookmark.user, OrderBy(Bookmark.createdAt, Descending))
  object notifications extends MappedOneToMany(Notification, Notification.user, OrderBy(Notification.createdAt, Descending))

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