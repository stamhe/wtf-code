package wtfcode.util

import wtfcode.model.{LastSeen, Comment}
import net.liftweb.util.Helpers._
import net.liftweb.textile.TextileParser
import net.liftweb.http.S

object CommentBinder {
  def apply(comment: Comment) = {
    val ratingTemplate = S.runTemplate(List("templates-hidden", "rating")).open_!
    ".comment-text" #> TextileParser.toHtml(comment.content) &
    ".author" #> comment.author.map(_.nickName.get).openOr("Guest") &
    ".date" #> comment.createdAt &
    ".comment-rating *" #> RateBinder(comment)(ratingTemplate) &
    ".avatar [src]" #> Avatar(comment.author, comment.ipAddress) &
    ".author-link [href]" #> comment.author.map(_.link).openOr("#") &
    ".comment-link [href]" #> comment.link &
    ".unseen [class]" #> unseen(comment) &
    ".anchor-link [href]" #> comment.anchor
  }

  private def unseen(comment: Comment) =
    if (LastSeen.unseen(comment)) "unseen" else ""
}
