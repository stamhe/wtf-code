package wtfcode.util

import xml.NodeSeq
import wtfcode.model.Comment
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
      AttrBindParam("avatar_url", RoboHash.fromIp(comment.ipAddress), "src"),
      AttrBindParam("link_to_author", comment.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_comment", comment.link, "href"),
      AttrBindParam("anchor", comment.anchor, "id"))
  }
}
