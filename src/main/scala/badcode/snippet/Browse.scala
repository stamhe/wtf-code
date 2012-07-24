package badcode.snippet

import xml.NodeSeq
import badcode.model.BadCode
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.PaginatorSnippet
import net.liftweb.mapper.{MaxRows, StartAt}

class Browse extends PaginatorSnippet[BadCode] {
  override def itemsPerPage = 20
  override def count = BadCode.count
  override def page = BadCode.findAll(StartAt(curPage * itemsPerPage), MaxRows(itemsPerPage))

  def renderPage(in: NodeSeq): NodeSeq =
    page.flatMap(code => Helpers.bind("entry", in,
      "content" -> code.content,
      "description" -> code.description,
      "author" -> code.author,
      "date" -> code.createdAt))
}
