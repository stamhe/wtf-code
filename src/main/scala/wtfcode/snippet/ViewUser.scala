package wtfcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.S
import wtfcode.model.User
import net.liftweb.mapper.By
import xml.{Text, NodeSeq}
import net.liftweb.textile.TextileParser

class ViewUser {
  val nick = S.param("nick") openOr ""

  val user = User.find(By(User.nickName, nick))

  def profile(in: NodeSeq): NodeSeq = {
    user map ({
      i => bind("entry", in,
        "nick" -> i.nickName,
        "date" -> i.createdAt,
        "aboutMe" -> TextileParser.toHtml(i.aboutMe.get))
    }) openOr Text("Not found")
  }
}
