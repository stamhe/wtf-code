package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{LastSeen, User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util.{JqRemoveClass, JqAddClass, CommentBinder, CodeBinder}
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJE.{JqRemove, JqId}

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
    val ret = code.open_!.comments.flatMap(
      comment => CommentBinder(in, comment)
    )
    LastSeen.update(User.currentUser, code)
    ret
  }

  lazy val commentTemplate = S.runTemplate("templates-hidden" :: "comment" :: Nil).open_!

  private val DeletePreviewCmd = (JqId("preview") ~> JqRemove()).cmd
  private val EnableAddCommentButton = JsRaw("""wtfCode_enableAddCommentButton()""").cmd

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def createComment(): Comment = {
      Comment.create.author(User.currentUser).post(code).content(content)
    }

    def process(func: () => JsCmd): JsCmd = {
      clearErrors() &
      (content.trim.length match {
        case 0 => compilationError(S ? "comment.commentNotFound")
        case _ => func()
      })
    }

    def processAdd(): JsCmd = {
      val newComment = createComment()
      newComment.save

      DeletePreviewCmd &
      EnableAddCommentButton &
      JsHideId("add-comment") &
      AppendHtml("comments", CommentBinder(commentTemplate, newComment))
    }

    def processPreview(): JsCmd = {
      val newComment = createComment()

      DeletePreviewCmd &
      AppendHtml("comments", <div id="preview"/>) &
      SetHtml("preview", CommentBinder(commentTemplate, newComment))
    }

    def compilationError(s: String): JsCmd = {
      SetHtml("content-inline-help", Text(S ? "comment.compilationError" + ": " + s)) &
      (JqId("content-group") ~> JqAddClass(Str ("error"))).cmd
    }

    def clearErrors(): JsCmd = {
      SetHtml("content-inline-help", Text("")) &
      (JqId("content-group") ~> JqRemoveClass(Str ("error"))).cmd
    }

    SHtml.ajaxForm(
      bind("entry", in,
        "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
        "submit" -> SHtml.ajaxSubmit(S ? "comment.add", () => process(processAdd), "class" -> "btn btn-primary"),
        "preview" -> SHtml.ajaxSubmit(S ? "comment.preview", () => process(processPreview), "class" -> "btn")
      )
    )
  }
}
