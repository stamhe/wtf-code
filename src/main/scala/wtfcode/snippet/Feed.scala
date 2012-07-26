package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.Comment
import net.liftweb.mapper.{MaxRows, Descending, OrderBy}

class Feed {
  val LIMIT = 20
  val comments = Comment.findAll(OrderBy(Comment.createdAt, Descending), MaxRows(LIMIT))

  def comments(in: NodeSeq): NodeSeq = {
    comments.flatMap(comment => bind("entry", in,
      "content" -> comment.content,
      "author" -> comment.author,
      "date" -> comment.createdAt))
  }
}
