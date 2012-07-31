package wtfcode.snippet

import xml.{Text, NodeSeq}
import net.liftweb.util.Helpers._
import wtfcode.model.{Language, Post}
import net.liftweb.mapper.{Descending, OrderBy, By}
import net.liftweb.http.{SHtml, S, PaginatorSnippet}
import wtfcode.util.CodeBinder
import net.liftweb.common.{Full, Box, Empty}

/**
 * List of filters by language.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class LanguageFilter extends PaginatorSnippet[Post] {

  override def itemsPerPage = 20
  override def count = Post.count(By(Post.language, Language.find(By(Language.name, lang))))
  override def page = Post.findAll(By(Post.language, Language.find(By(Language.name, lang))), OrderBy(Post.id, Descending))

  private val lang = S.param("lang") openOr ""

  def render(xhtml: NodeSeq): NodeSeq = {
    Language.orderedByPopularity().flatMap { l =>
      bind("lang", xhtml,
        "name" -> l.name.is,
        "count" -> l.postNumber.is,
        AttrBindParam("lang_filter_link", l.link, "href")
    )}
  }

  def renderPage(xhtml: NodeSeq): NodeSeq = {
    page.flatMap { p => CodeBinder(xhtml, p) }
  }
}
