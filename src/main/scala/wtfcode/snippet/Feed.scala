package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Comment
import net.liftweb.util.Helpers._
import net.liftweb.mapper._
import wtfcode.util.{ CommentBinder }

class Feed extends BootstrapPaginatorSnippet[Comment] {
  override def itemsPerPage = 20
  override def count = Comment.count
  override def page = Comment.findAll(
    By(Comment.deleted, false),
    By_>=(Comment.rating, Comment.MinRating),
    OrderBy(Comment.createdAt, Descending),
    StartAt(curPage * itemsPerPage),
    MaxRows(itemsPerPage))

  def renderPage =
    ".comments *" #> ((in: NodeSeq) => page.flatMap { comment => CommentBinder(comment)(in) })
}
