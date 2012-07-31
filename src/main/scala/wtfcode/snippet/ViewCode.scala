package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util.{CommentBinder, CodeBinder}
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    Post.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    code map ({
      i => CodeBinder(in, i)
    }) openOr Text("Not found")
  }

  def comments(in: NodeSeq): NodeSeq = {
    code.open_!.comments.flatMap(
      comment => CommentBinder(in, comment)
    )
  }

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def createComment(): Comment = {
      Comment.create.author(User.currentUser).post(code).content(content)
    }

    def processAdd(): JsCmd = {
      val newComment = createComment()
      newComment.save()

      val template = S.runTemplate("templates-hidden" :: "comment" :: Nil)
      AppendHtml("comments", CommentBinder(template.open_!, newComment))
    }

    def processPreview(): JsCmd = {
      val newComment = createComment()

      val template = S.runTemplate("templates-hidden" :: "comment" :: Nil)
      SetHtml("preview", CommentBinder(template.open_!, newComment))
    }

    SHtml.ajaxForm(
      bind("entry", in,
        "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
        "submit" -> SHtml.ajaxSubmit(S ? "comment.add", () => processAdd()),
        "preview" -> SHtml.ajaxSubmit(S ? "comment.preview", () => processPreview())
      )
    )
  }
}
