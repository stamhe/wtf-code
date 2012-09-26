package wtfcode {
package model {

import net.liftweb.mapper._
import net.liftweb.http.S
import net.liftweb.common.{Box, Full}
import net.liftweb.util.FieldError
import xml.{NodeSeq, Elem, Text}
import util.BootstrapForms._

class User extends MegaProtoUser[User] with CreatedTrait with OneToMany[Long, User] {
  def getSingleton = User

  val nickNameLength = 16

  override lazy val password = new MyPassword(this) {

    override def _toForm: Box[NodeSeq] = {
      S.fmapFunc({s: List[String] => this.setFromAny(s)}){ funcName =>
        Full(
          controlGroup(displayName, appendFieldId(<input type={formInputType} name={funcName} value={is.toString}/>)) ++
            controlGroup(S ? "repeat", <input type={formInputType} name={funcName} value={is.toString}/>)
        )
      }
    }
  }

  object nickName extends MappedString(this, nickNameLength) {
    override def displayName = S ? "user.nickname"
  }

  object nickNameLower extends MappedString(this, nickNameLength) {
    override def dbIndexed_? = true
  }

  object aboutMe extends MappedTextarea(this, 1024) {
    override def displayName = S ? "user.aboutMe"
    override def textareaRows = 10
    override def textareaCols = 80
  }
  object bookmarks extends MappedOneToMany(Bookmark, Bookmark.user, OrderBy(Bookmark.createdAt, Descending))
  object notifications extends MappedOneToMany(Notification, Notification.user, OrderBy(Notification.createdAt, Descending))

  def link = "/user/" + id

  def findByNickName(nick: String): Box[User] = {
    User.find(By(User.nickNameLower, nick.toLowerCase))
  }
}

object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users"

  override def screenWrap = Full(<lift:surround with="default" at="content">
    <lift:bind/></lift:surround>)

  override val skipEmailValidation = true //FIXME

  override def signupFields = List(nickName, email)

  override def editFields = List(email, locale, timezone, aboutMe)

  onLogIn = List(ExtSession.userDidLogin(_))
  onLogOut = List(ExtSession.userDidLogout(_))

  override protected def actionsAfterSignup(theUser: User, func: () => Nothing): Nothing = {
    theUser.nickNameLower(theUser.nickName.toLowerCase)
    super.actionsAfterSignup(theUser, func)
  }

  override def validateSignup(user: User): List[FieldError] = {
    def reserved(value: String): List[FieldError] = {
      value match {
        case "Guest" => List(FieldError(user.nickName, Text(S ? "user.nicknameReserved")))
        case _ => Nil
      }
    }

    def allowedChars(value: String): List[FieldError] = {
      value.matches("(\\p{Alnum}|[+-|_]){3,}") match {
        case true => Nil
        case false => List(FieldError(user.nickName, Text(S ? "user.nicknameBadChars")))
      }
    }

    def unique(value: String): List[FieldError] = {
      findByNickName(value) match {
        case Full(_) => List(FieldError(user.nickName, Text(S ? "user.uniqueNickname")))
        case _ => Nil
      }
    }
    val nickName = user.nickName.is

    user.validate ++ reserved(nickName) ++ allowedChars(nickName) ++ unique(nickName)
  }

  // UI specification

  override def lostPasswordXhtml =
    (<form method="post" action={S.uri} class="form-horizontal">
      <legend>{S ? "lost.password"}</legend>
      {controlGroup(userNameFieldString, <user:email/>)}
      {controlGroup(<user:submit/>)}
    </form>)

  override def loginXhtml = {
    val recoverPasswordLink = (<a href={lostPasswordPath.mkString("/", "/", "")}>{S.?("recover.password")}</a>)

    <form method="post" action={S.uri} class="form-horizontal">
      <legend>{S ? "log.in"}</legend>
      {controlGroup(userNameFieldString, <user:email/>)}
      {controlGroup(S ? "password", <user:password/>)}
      {controlGroup(recoverPasswordLink ++ <br/>)}
      {formActions(<user:submit/>)}
    </form>
  }

  override def signupXhtml(user: User): Elem = {
    val passFields = computeFieldFromPointer(user, password).toList

    <form method="post" action={S.uri} class="form-horizontal">
      <legend>{S ? "sign.up"}</legend>
      {localForm(user = user, ignorePassword = false, fields = signupFields)}
      {for (f <- passFields; field <- f.toForm.toList) yield field}
      {formActions(<user:submit/>)}
    </form>
  }

  override def editXhtml(user: TheUserType) =
    <form method="post" action={S.uri} class="form-horizontal">
      <legend>{S ? "edit"}</legend>
      {localForm(user = user, ignorePassword = true, fields = editFields)}
      {formActions(<user:submit/>)}
    </form>

  override def changePasswordXhtml =
    <form method="post" action={S.uri} class="form-horizontal">
      <legend>{S ? "change.password"}</legend>
      {controlGroup(S ? "old.password", <user:old_pwd />)}
      {controlGroup(S ? "new.password", <user:new_pwd />)}
      {controlGroup(S ? "repeat.password", <user:new_pwd />)}
      {formActions(<user:submit/>)}
    </form>

  override def localForm(user: TheUserType, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq = {
    for {
      pointer <- fields
      field <- computeFieldFromPointer(user, pointer).toList
      if field.show_? && (!ignorePassword || !pointer.isPasswordField_?)
      form <- field.toForm.toList
    } yield controlGroup(field.displayName, form)
  }
}

}
}