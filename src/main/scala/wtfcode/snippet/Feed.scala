package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Comment
import net.liftweb.mapper.{MaxRows, Descending, OrderBy}
import wtfcode.util.CommentBinder

class Feed {
  val LIMIT = 20
  val comments = Comment.findAll(OrderBy(Comment.createdAt, Descending), MaxRows(LIMIT))

  def comments(in: NodeSeq): NodeSeq = {
    comments.flatMap(
      comment => CommentBinder(in, comment)
    )
  }
}
