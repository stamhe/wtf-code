package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Post
import net.liftweb.http.PaginatorSnippet
import net.liftweb.mapper.{MaxRows, StartAt}
import wtfcode.util.CodeBinder

class Browse extends PaginatorSnippet[Post] {
  override def itemsPerPage = 20
  override def count = Post.count
  override def page = Post.findAll(StartAt(curPage * itemsPerPage), MaxRows(itemsPerPage))

  def renderPage(in: NodeSeq): NodeSeq =
    page.flatMap(code => CodeBinder(in, code))
}
