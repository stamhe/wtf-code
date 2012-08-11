package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model._
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util._
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import wtfcode.util.JqAddClass
import wtfcode.util.JqRemoveClass
import net.liftweb.http.js.jquery.JqJE.JqId
import xml.Text
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.jquery.JqJE.JqRemove
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JE.JsRaw

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    Post.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    code map ({
      i => CodeBinder(i)(in)
    }) openOr Text("Not found")
  }

  def comments() = {
    "#comments *" #> ((in: NodeSeq) =>
      code.map(_.comments.filter(_.responseTo.isEmpty).flatMap { comment => CommentBinder(comment)(in) }).openOr(NodeSeq.Empty)
    )
  }

  def updateUnseen() = {
    LastSeen.update(User.currentUser, code)
    NodeSeq.Empty
  }

  lazy val commentTemplate = S.runTemplate("templates-hidden" :: "comment" :: Nil).openOrThrowException("template must exist")

  private val DeletePreviewCmd = (JqId("preview") ~> JqRemove()).cmd
  private val EnableAddCommentButton = JsRaw("""Comments.enableAddButton()""").cmd

  def addComment() = {
    code.isDefined match {
      case true => addCommentReal()
      case false => "#add-comment" #> NodeSeq.Empty
    }
  }

  private def addCommentReal() = {
    var content = ""

    def createComment(): Comment = {
      val parentId = S.param("parentId").openOr("0").toLong
      val parent = Comment.findByKey(parentId)
      Comment.create.author(User.currentUser).post(code).content(content).responseTo(parent)
    }

    def process(func: () => JsCmd): JsCmd = {
      clearErrors() &
      (content.trim.length match {
        case 0 => compilationError(S ? "comment.commentNotFound")
        case _ => func()
      })
    }

    def processAdd(): JsCmd = {
      val newComment = createComment().saveMe()
      Notification.send(newComment)

      DeletePreviewCmd &
      EnableAddCommentButton &
      JsHideId("add-comment") &
      AppendHtml("comments", CommentBinder(newComment)(commentTemplate)) &
      SyntaxHighlighter.highlightPage()
    }

    def processPreview(): JsCmd = {
      val newComment = createComment()

      DeletePreviewCmd &
      AppendHtml("comments", <li id="preview"/>) &
      SetHtml("preview", CommentBinder(newComment)(commentTemplate)) &
      SyntaxHighlighter.highlightBlock("preview")
    }

    def compilationError(s: String): JsCmd = {
      SetHtml("errors", Text(S ? "comment.compilationError" + ": " + s)) &
      (JqId("errors") ~> JqAddClass(Str ("compile-error"))).cmd
    }

    def clearErrors(): JsCmd = {
      SetHtml("errors", Text("")) &
      (JqId("errors") ~> JqRemoveClass(Str ("compile-error"))).cmd
    }

    ".content" #> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8") &
    ".submit" #> SHtml.ajaxSubmit(S ? "comment.add", () => process(processAdd), "class" -> "btn btn-primary") &
    ".preview" #> SHtml.ajaxSubmit(S ? "comment.preview", () => process(processPreview), "class" -> "btn")
  }
}
