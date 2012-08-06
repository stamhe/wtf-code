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
  override def count = Post.count(searchCondition)
  override def page = Post.findAll(searchCondition, OrderBy(Post.id, Descending))

  private val lang = S.param("lang") openOr ""
  private val searchCondition = By(Post.language, Language.find(By(Language.code, lang)))

  def render() = {
    val langs = Language.orderedByPopularity()
    ".lang-filters *" #> ((ns: NodeSeq) =>
      langs.flatMap { l =>
        (".name *" #> l.name.is &
          ".count *" #> l.postNumber.is &
          ".lang-filter-link [href]" #> l.link)(ns)
      })
  }

  def renderPage(xhtml: NodeSeq): NodeSeq = {
    page.flatMap { p => CodeBinder(xhtml, p) }
  }
}
