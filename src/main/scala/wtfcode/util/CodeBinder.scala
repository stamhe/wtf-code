package wtfcode.util

import xml.NodeSeq
import wtfcode.model.Post
import net.liftweb.util.Helpers._
import net.liftweb.textile.TextileParser

object CodeBinder {
  def apply(template: NodeSeq, post: Post): NodeSeq = {
    bind("entry", template,
      "id" -> post.id,
      "language" -> post.getLanguage,
      "content" -> post.content,
      "description" -> TextileParser.toHtml(post.description),
      "author" -> post.author.map(_.nickName.get).openOr("Guest"),
      "date" -> post.createdAt,
      "commentsNum" -> post.comments.size,
      AttrBindParam("link_to_author", post.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_code", post.link, "href"))
  }
}
