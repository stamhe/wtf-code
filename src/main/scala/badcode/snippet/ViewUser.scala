package badcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.S
import badcode.model.{User, BadCode}
import net.liftweb.common.Empty
import net.liftweb.mapper.By
import xml.{Text, NodeSeq}

class ViewUser {
  val nick = S.param("nick") openOr ""

  val user = User.find(By(User.nickName, nick))

  def profile(in: NodeSeq): NodeSeq = {
    user map ({
      i => bind("entry", in,
        "nick" -> i.nickName,
        "date" -> i.createdAt)
    }) openOr Text("Not found")
  }
}
