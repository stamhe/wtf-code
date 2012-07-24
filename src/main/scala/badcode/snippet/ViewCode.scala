package badcode.snippet

import xml.{Text, NodeSeq}
import badcode.model.BadCode
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.S
import net.liftweb.common.Empty

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    BadCode.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    code map ({
      i => bind("entry", in,
        "content" -> i.content,
        "description" -> i.description,
        "author" -> i.author,
        "date" -> i.createdAt)
    }) openOr Text("Not found")
  }
}
