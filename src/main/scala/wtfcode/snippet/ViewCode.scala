package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util.{CommentBinder, CodeBinder}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml

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

  def vote(in: NodeSeq): NodeSeq = {
    code.map { post =>
      SHtml.ajaxForm(
        bind("vote", in,
          "rating" -> post.rating,
          "voteOn" -> SHtml.a(() => applyVote(post.voteOn _), Text("++")),
          "voteAgainst" -> SHtml.a(() => applyVote(post.voteAgainst _), Text("--"))
        )
    )
    } openOr {
      bind("vote", in,
        "voteOn" -> Text("++"),
        "rating" -> Text("0"),
        "voteAgainst" -> Text("--")
      )
    }
  }

  def applyVote(update: User => Int) = {
    val maybeUser = User.currentUser
    val post = code.open_!
    val newValue: Int = if(maybeUser.isEmpty || !post.canVote(maybeUser.open_!)) post.rating else {
      update(maybeUser.open_!)
    }
    SetHtml("post-rating-value", Text(newValue.toString))
  }

  def comments(in: NodeSeq): NodeSeq = {
    code.open_!.comments.flatMap(
      comment => CommentBinder(in, comment)
    )
  }

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def createComment(): Comment = {
      val comment = Comment.create.author(User.currentUser).post(code).content(content)
      comment.save()
      comment
    }

    SHtml.ajaxForm(
      bind("entry", in,
        "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
        "submit" -> SHtml.ajaxSubmit(S ? "comment.add", () => {
          val newComment = createComment()
          val template = S.runTemplate("templates-hidden" :: "comment" :: Nil)
          AppendHtml("comments", CommentBinder(template.open_!, newComment))
        })
      )
    )
  }
}
