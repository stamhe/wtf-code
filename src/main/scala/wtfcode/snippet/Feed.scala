package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Comment
import net.liftweb.mapper.{StartAt, MaxRows, Descending, OrderBy}
import wtfcode.util.CommentBinder
import net.liftweb.http.PaginatorSnippet

class Feed extends PaginatorSnippet[Comment] {
  override def itemsPerPage = 20
  override def count = Comment.count
  override def page = Comment.findAll(
    OrderBy(Comment.createdAt, Descending),
    StartAt(curPage * itemsPerPage),
    MaxRows(itemsPerPage))

  def renderPage(in: NodeSeq): NodeSeq =
    page.flatMap(comment => CommentBinder(in, comment))
}
