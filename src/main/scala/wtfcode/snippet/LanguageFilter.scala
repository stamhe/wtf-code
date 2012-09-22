package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.util.Helpers._
import wtfcode.model.{Language, Post}
import net.liftweb.mapper._
import net.liftweb.http.S
import wtfcode.util.AjaxCommentsCodeBinder

/**
 * List of filters by language.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class LanguageFilter extends BootstrapPaginatorSnippet[Post] {

  override def itemsPerPage = 20
  override def count = Post.count(searchCondition, ratingFilter, deletedFilter)
  override def page = Post.findAll(searchCondition, ratingFilter, deletedFilter,
      OrderBy(Post.id, Descending), StartAt(curPage * itemsPerPage), MaxRows(itemsPerPage))

  private val lang = S.param("lang") openOr ""
  private val searchCondition = By(Post.language, Language.find(By(Language.code, lang)))
  private val ratingFilter = By_>=(Post.rating, Post.MinRating)
  private val deletedFilter =  By(Post.deleted, false)

  def render() = {
    val langs = Language.orderedByPopularity()
    ".lang-filters *" #> ((ns: NodeSeq) =>
      langs.flatMap { l =>
        (".name *" #> l.name.is &
          ".count *" #> l.postNumber.is &
          ".lang-filter-link [href]" #> l.link)(ns)
      })
  }

  def renderPage() =
    ".posts *" #> ((in: NodeSeq) => page.flatMap { p => AjaxCommentsCodeBinder(p)(in) })
}
