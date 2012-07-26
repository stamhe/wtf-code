package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    Post.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    code map ({
      i => bind("entry", in,
        "language" -> i.getLanguage,
        "content" -> i.content,
        "description" -> i.description,
        "author" -> i.author,
        "date" -> i.createdAt)
    }) openOr Text("Not found")
  }

  def comments(in: NodeSeq): NodeSeq = {
    code.open_!.comments.flatMap(
      comment => bind("entry", in,
        "content" -> comment.content,
        "author" -> comment.author.map(_.nickName.toString).openOr("Guest"),
        "date" -> comment.createdAt,
        AttrBindParam("link_to_author", comment.author.map(_.link).openOr("#"), "href"),
        AttrBindParam("link_to_comment", comment.link, "href"),
        AttrBindParam("anchor", comment.anchor, "id"))
    )
  }

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def processAddComment() {
      Comment.create.author(User.currentUser).post(code).content(content).save()
    }

    bind("entry", in,
      "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
      "submit" -> SHtml.submit("Add", processAddComment))
  }
}
