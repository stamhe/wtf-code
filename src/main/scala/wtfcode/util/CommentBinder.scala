package wtfcode.util

import xml.NodeSeq
import wtfcode.model.{LastSeen, Comment}
import net.liftweb.util.Helpers._
import net.liftweb.textile.TextileParser
import net.liftweb.http.S

object CommentBinder {
  def apply(template: NodeSeq, comment: Comment): NodeSeq = {
    bind("entry", template,
      "content" -> TextileParser.toHtml(comment.content),
      "author" -> comment.author.map(_.nickName.get).openOr("Guest"),
      "date" -> comment.createdAt,
      "rate" -> RateBinder(S.runTemplate(List("templates-hidden", "rating")).open_!, comment),
      AttrBindParam("avatar_url", Avatar(comment.author, comment.ipAddress), "src"),
      AttrBindParam("link_to_author", comment.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_comment", comment.link, "href"),
      AttrBindParam("unseen", unseen(comment), "class"),
      AttrBindParam("anchor", comment.anchor, "id"))
  }

  private def unseen(comment: Comment): String = {
    if (LastSeen.unseen(comment))
      "unseen"
    else
      ""
  }
}
