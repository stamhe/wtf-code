package wtfcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.S
import wtfcode.model.User
import xml.{Text, NodeSeq}
import wtfcode.util.{WtfBbParser, Avatar}
import net.liftweb.common.{Empty, Full}

class ViewUser {
  val maybeId = asLong(S.param("id"))

  val maybeUser = maybeId match {
    case Full(id) => User.findByKey(id)
    case _ => Empty
  }

  def profile = {
    maybeUser match {
      case Full(user) =>
        ".nick" #> user.nickName &
        ".avatar [src]" #> Avatar(maybeUser, null) &
        ".date" #> user.createdAt &
        ".aboutMe" #> WtfBbParser.toHtml(user.aboutMe.get)
      case _ => (in: NodeSeq) => Text("Not Found")
    }
  }
}
