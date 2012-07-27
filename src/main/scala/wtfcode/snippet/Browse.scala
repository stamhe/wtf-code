package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.Post
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.PaginatorSnippet
import net.liftweb.mapper.{MaxRows, StartAt}
import net.liftweb.textile.TextileParser

class Browse extends PaginatorSnippet[Post] {
  override def itemsPerPage = 20
  override def count = Post.count
  override def page = Post.findAll(StartAt(curPage * itemsPerPage), MaxRows(itemsPerPage))

  def renderPage(in: NodeSeq): NodeSeq =
    page.flatMap(code => bind("entry", in,
      "id" -> code.id,
      "language" -> code.getLanguage,
      "content" -> code.content,
      "description" -> TextileParser.toHtml(code.description),
      "date" -> code.createdAt,
      "author" -> code.author.map(_.nickName.toString).openOr("Guest"),
      AttrBindParam("link_to_author", code.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_code", code.link, "href")))
}
