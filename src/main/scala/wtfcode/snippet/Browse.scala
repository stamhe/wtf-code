package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Post
import net.liftweb.util.Helpers._
import net.liftweb.http.PaginatorSnippet
import net.liftweb.mapper.{Descending, OrderBy, MaxRows, StartAt}
import wtfcode.util.CodeBinder

class Browse extends PaginatorSnippet[Post] {
  override def itemsPerPage = 20
  override def count = Post.count
  override def page = Post.findAll(
    OrderBy(Post.createdAt, Descending),
    StartAt(curPage * itemsPerPage),
    MaxRows(itemsPerPage))

  def renderPage() =
    ".posts *" #> ((in: NodeSeq) => page.flatMap { code => CodeBinder(code)(in) })
}
