package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{LastSeen, User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util.{CommentBinder, CodeBinder}
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    Post.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    LastSeen.update(User.currentUser, code)
    code map ({
      i => CodeBinder(in, i)
    }) openOr Text("Not found")
  }

  def comments(in: NodeSeq): NodeSeq = {
    code.open_!.comments.flatMap(
      comment => CommentBinder(in, comment)
    )
  }

  lazy val commentTemplate = S.runTemplate("templates-hidden" :: "comment" :: Nil).open_!

  private val DeletePreviewCmd = JsRaw(""" $("#preview").remove(); """).cmd

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def createComment(): Comment = {
      Comment.create.author(User.currentUser).post(code).content(content)
    }

    def processAdd(): JsCmd = {
      val newComment = createComment()
      newComment.save

      DeletePreviewCmd &
      AppendHtml("comments", CommentBinder(commentTemplate, newComment))
    }

    def processPreview(): JsCmd = {
      val newComment = createComment()

      DeletePreviewCmd &
      AppendHtml("comments", <div id="preview"/>) &
      SetHtml("preview", CommentBinder(commentTemplate, newComment))
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
