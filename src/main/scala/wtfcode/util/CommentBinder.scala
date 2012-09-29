package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model.{Post, LastSeen, Comment}
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds
import net.liftweb.common.Full
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds.SetHtml

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
        <p style="color: red;">{S ? "moderator.deletedBy"}</p>
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

  trait Headers extends Defaults {
    override def apply(comment: Comment): CssSel = {
      val postLink = Post.linkTo(comment.post.is)
      super.apply(comment) &
        ".comment-header *" #> <a href={postLink}>{S.?("comment.header", comment.post.is)}</a>
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

  trait PostPreview extends Defaults {
    lazy val PostTemplate = S.runTemplate(List("templates-hidden", "code"))
      .openOrThrowException("Post template doesn't exists")

    override def apply(comment: Comment): CssSel = {
      val post = comment.post.openOrThrowException("Post not found")
      val previewDivId = "post_" + post.id.is + "_" + comment.id.is + "_preview"
      super.apply(comment) &
        ".post-preview [id]" #> previewDivId &
        ".post-preview-link *" #> SHtml.a(
          () => {
            SetHtml(previewDivId, CodeBinder(post)(PostTemplate)) &
              SyntaxHighlighter.highlightBlock(previewDivId)
          }, <span>{S ? "comment.viewPost"}</span>)
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

object PostPreviewCommentBinder extends CommentBinder with PostPreview with Headers

object RecursiveCommentBinder extends CommentBinder with RecursiveReplies
