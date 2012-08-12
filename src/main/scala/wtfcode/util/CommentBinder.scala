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

  private lazy val ratingTemplate = 
    S.runTemplate(List("templates-hidden", "rating")).openOrThrowException("template must exist")

  private def defaultBindings(comment: Comment): CssSel = {
    ".comment-text *" #> TextileParser.toHtml(comment.content) &
      ".author" #> comment.author.map(_.nickName.get).openOr("Guest") &
      ".date" #> comment.createdAt &
      ".comment-rating *" #> RateBinder(comment)(ratingTemplate) &
      ".avatar [src]" #> Avatar(comment.author, comment.ipAddress) &
      ".author-link [href]" #> comment.author.map(_.link).openOr("#") &
      ".comment-link [href]" #> comment.link &
      ".parent-link [href]" #> comment.responseTo.map(_.link).openOr("#") &
      ".unseen [class]" #> unseen(comment) &
      ".comment [id]" #> comment.anchor
  }

  def applyRecursively(comment: Comment): (NodeSeq => NodeSeq) = {
    defaultBindings(comment) & repliesBindings(comment)
  }

  def apply(comment: Comment) = defaultBindings(comment)

  private def unseen(comment: Comment): Option[String] =
    if (LastSeen.unseen(comment)) Some("unseen") else None

  private def repliesBindings(c: Comment): CssSel = {
    ".replies *" #> ((in: NodeSeq) =>
      c.answers.flatMap(a => CommentBinder.applyRecursively(a)(commentTemplate)))
  }
}
