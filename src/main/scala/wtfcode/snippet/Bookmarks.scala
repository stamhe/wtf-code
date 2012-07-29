package wtfcode.snippet

import net.liftweb.http.PaginatorSnippet
import wtfcode.model.{Bookmark, User}
import xml.NodeSeq
import wtfcode.util.CodeBinder

class Bookmarks extends PaginatorSnippet[Bookmark] {
  val startAt = curPage * itemsPerPage

  override def itemsPerPage = 20
  override def count = User.currentUser.map(_.bookmarks.size.toLong).openOr(0)
  override def page = User.currentUser.map(_.bookmarks.slice(startAt, startAt + itemsPerPage)).openOr(Nil)

  def renderPage(in: NodeSeq): NodeSeq =
    page.flatMap(bookmark => CodeBinder(in, bookmark.post.open_!))
}