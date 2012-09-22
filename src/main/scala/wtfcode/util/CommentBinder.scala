package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model.{Post, LastSeen, Comment}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds
import net.liftweb.common.Full
import net.liftweb.util.CssSel

object CommentBinders {

  protected[wtfcode] lazy val CommentTemplate = S.runTemplate(List("templates-hidden", "comment"))
    .openOrThrowException("Comment template doesn't exist")

  trait BindStrategy {
    def apply(comment: Comment): CssSel
  }

  class Defaults extends BindStrategy {
    override def apply(comment: Comment): CssSel = {
      ".comment-text *" #> bindCommentContent(comment) &
        ".author" #> comment.author.map(_.nickName.get).openOr("Guest") &
        ".date" #> TimeSpanFormatter(comment.createdAt) &
        ".avatar [src]" #> Avatar(comment.author, Full(comment.ipAddress)) &
        ".author-link [href]" #> comment.author.map(_.link).openOr("#") &
        ".comment-link [href]" #> comment.link &
        ".parent-link [href]" #> comment.responseTo.map(_.link).openOr("#") &
        ".unseen [class]" #> unseen(comment) &
        ".replies [id]" #> comment.repliesAnchor &
        ".comment [id]" #> comment.anchor
    }

    private def unseen(comment: Comment): Option[String] =
      if (LastSeen.unseen(comment)) Some("unseen") else None

    private def bindCommentContent(comment: Comment): NodeSeq = {
      if (comment.deleted.is)
        <p style="color: red;">{S ? "comment.deleted"}</p>
      else
        WtfBbParser.toHtml(comment.content.is)
    }
  }

  trait Rating extends Defaults {
    private lazy val RatingTemplate =
      S.runTemplate(List("templates-hidden", "rating"))
        .openOrThrowException("Rating template doesn't exist")

    override def apply(comment: Comment): CssSel = {
      super.apply(comment) & ".comment-rating *" #> RateBinder(comment)(RatingTemplate)
    }
  }

  trait RecursiveReplies extends Defaults {
    override def apply(comment: Comment): CssSel = {
    val replyHtml =
      (<a class="reply" data-comment-id={ comment.id.is.toString }>{ S ? "comment.reply" }</a>
        <a class="foldSubtree">{ S ? "comment.fold" }</a>
        <a class="expandSubtree" style="display: none;">{ S ? "comment.expand" }</a>)

    super.apply(comment) &
      (if (comment.answers.isEmpty)
        ".reply-container" #> replyHtml
      else
        ".reply-container" #> replyHtml &
          ".replies *" #> ((in: NodeSeq) =>
            comment.answers.flatMap(a => apply(a)(CommentTemplate))))
    }
  }

  trait CommentDelete extends Defaults {
    override def apply(comment: Comment): CssSel = {
      super.apply(comment) &
        (if (comment.canDelete)
          "#delete-comment" #> SHtml.a(() => {comment.delete(); JsCmds.Noop}, Text("!DELETE!"))
        else
          "#delete-comment" #> NodeSeq.Empty)
    }
  }

  def applyToRoots(strategy: BindStrategy, post: Post): NodeSeq =
    post.comments.filter(_.responseTo.isEmpty).flatMap { comment =>
      strategy(comment)(CommentTemplate)
    }
}

import CommentBinders._

class CommentBinder extends Defaults with Rating with CommentDelete

object CommentBinder extends CommentBinder

object RecursiveCommentBinder extends CommentBinder with RecursiveReplies
