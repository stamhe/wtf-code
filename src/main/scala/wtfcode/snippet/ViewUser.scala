package wtfcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{NotFoundResponse, S}
import wtfcode.model.User
import net.liftweb.mapper.By
import xml.{Text, NodeSeq}
import wtfcode.util.Avatar
import net.liftweb.common.Full
import scabb.BbParser

class ViewUser {
  val nick = S.param("nick") openOr ""

  val maybeUser = User.find(By(User.nickName, nick))

  def profile = {
    maybeUser match {
      case Full(user) =>
        ".nick" #> user.nickName &
        ".avatar [src]" #> Avatar(maybeUser, null) &
        ".date" #> user.createdAt &
        ".aboutMe" #> BbParser.toHtml(user.aboutMe.get)
      case _ => (in: NodeSeq) => Text("Not Found")
    }
  }
}
