package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model.{Post, LastSeen, Comment}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds
import net.liftweb.common.Full
import net.liftweb.util.CssSel

object CommentBinder {

  private lazy val CommentTemplate =
    S.runTemplate(List("templates-hidden", "comment")).openOrThrowException("comment template must exists!")

  private lazy val ratingTemplate =
    S.runTemplate(List("templates-hidden", "rating")).openOrThrowException("template must exist")

  private def defaultBindings(comment: Comment): CssSel = {
    ".comment-text *" #> bindCommentContent(comment) &
      ".author" #> comment.author.map(_.nickName.get).openOr("Guest") &
      ".date" #> TimeSpanFormatter(comment.createdAt) &
      ".comment-rating *" #> RateBinder(comment)(ratingTemplate) &
      ".avatar [src]" #> Avatar(comment.author, Full(comment.ipAddress)) &
      ".author-link [href]" #> comment.author.map(_.link).openOr("#") &
      ".comment-link [href]" #> comment.link &
      ".parent-link [href]" #> comment.responseTo.map(_.link).openOr("#") &
      bindDelete(comment) &
      ".unseen [class]" #> unseen(comment) &
      ".replies [id]" #> comment.repliesAnchor &
      ".comment [id]" #> comment.anchor
  }

  private def bindCommentContent(comment: Comment): NodeSeq = {
    if (comment.deleted.is)
      <p style="color: red;">{S ? "comment.deleted"}</p>
    else
      WtfBbParser.toHtml(comment.content.is)
  }

  private def bindDelete(comment: Comment): CssSel = {
    if (comment.canDelete)
      "#delete-comment" #> SHtml.a(() => {comment.delete(); JsCmds.Noop}, Text("!DELETE!"))
    else
      "#delete-comment" #> NodeSeq.Empty
  }

  def applyToPost(post: Post): NodeSeq =
    post.comments.filter(_.responseTo.isEmpty).flatMap { comment =>
      CommentBinder.applyRecursively(comment)(CommentTemplate)
    }

  def applyRecursively(comment: Comment): (NodeSeq => NodeSeq) = {
    defaultBindings(comment) & repliesBindings(comment)
  }

  def apply(comment: Comment) = defaultBindings(comment)

  private def unseen(comment: Comment): Option[String] =
    if (LastSeen.unseen(comment)) Some("unseen") else None

  private def repliesBindings(c: Comment): CssSel = {
    val replyHtml =
      (<a class="reply" data-comment-id={ c.id.is.toString }>{ S ? "comment.reply" }</a>
      <a class="foldSubtree">{ S ? "comment.fold" }</a>
      <a class="expandSubtree" style="display: none;">{ S ? "comment.expand" }</a>)
    if (c.answers.isEmpty)
      ".reply-container" #> replyHtml
    else
      ".reply-container" #> replyHtml &
        ".replies *" #> ((in: NodeSeq) =>
          c.answers.flatMap(a => CommentBinder.applyRecursively(a)(CommentTemplate)))
  }
}
