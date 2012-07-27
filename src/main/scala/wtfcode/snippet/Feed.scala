package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.Comment
import net.liftweb.mapper.{MaxRows, Descending, OrderBy}
import net.liftweb.textile.TextileParser
import wtfcode.util.RoboHash

class Feed {
  val LIMIT = 20
  val comments = Comment.findAll(OrderBy(Comment.createdAt, Descending), MaxRows(LIMIT))

  def comments(in: NodeSeq): NodeSeq = {
    comments.flatMap(
      comment => bind("entry", in,
      "content" -> TextileParser.toHtml(comment.content),
      "author" -> comment.author.map(_.nickName.toString).openOr("Guest"),
      "date" -> comment.createdAt,
      AttrBindParam("avatar_url", RoboHash.fromIp(comment.ipAddress), "src"),
      AttrBindParam("link_to_author", comment.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_comment", comment.link, "href"),
      AttrBindParam("anchor", comment.anchor, "id")
      )
    )
  }
}
