package wtfcode.util

import scala.xml.NodeSeq
import wtfcode.model.{ LastSeen, Comment }
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftmodules.textile.TextileParser
import net.liftweb.http.S

object CommentBinder {

  private lazy val commentTemplate =
    S.runTemplate(List("templates-hidden", "comment")).openOrThrowException("comment template must exists!")

  def apply(comment: Comment): (NodeSeq => NodeSeq) = {
    val ratingTemplate = S.runTemplate(List("templates-hidden", "rating")).openOrThrowException("template must exist")
    ".comment-text *" #> TextileParser.toHtml(comment.content) &
      ".author" #> comment.author.map(_.nickName.get).openOr("Guest") &
      ".date" #> comment.createdAt &
      ".comment-rating *" #> RateBinder(comment)(ratingTemplate) &
      ".avatar [src]" #> Avatar(comment.author, comment.ipAddress) &
      ".author-link [href]" #> comment.author.map(_.link).openOr("#") &
      ".comment-link [href]" #> comment.link &
      ".parent-link [href]" #> comment.responseTo.map(_.link).openOr("#") &
      ".unseen [class]" #> unseen(comment) &
      answersBindings(comment) &
      ".comment [id]" #> comment.anchor
  }

  private def unseen(comment: Comment): Option[String] =
    if (LastSeen.unseen(comment)) Some("unseen") else None

  private def answersBindings(c: Comment): CssSel = {
    ".answers *" #> ((in: NodeSeq) =>
      c.answers.flatMap(a => CommentBinder(a)(commentTemplate)))
  }
}
