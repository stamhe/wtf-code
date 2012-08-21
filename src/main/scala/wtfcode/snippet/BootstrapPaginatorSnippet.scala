package wtfcode.snippet

import scala.xml._
import net.liftweb.http.PaginatorSnippet
import net.liftweb.util.Helpers._

trait BootstrapPaginatorSnippet[T] extends PaginatorSnippet[T] {

  protected def prevClass = if (curPage < 2) "disabled" else ""
  protected def nextClass = if (curPage >= (numPages - 1)) "disabled" else ""

  override def pageXml(newFirst: Long, ns: NodeSeq): NodeSeq =
    if (first == newFirst || newFirst < 0 || newFirst >= count)
      <a href="#">{ ns }</a>
    else
      <a href={ pageUrl(newFirst) }>{ ns }</a>

   override def paginate(xhtml: NodeSeq) = {
    bind(navPrefix, xhtml,
         "first" -> pageXml(0, firstXml),
         "prev" -> pageXml(first-itemsPerPage max 0, prevXml),
         "allpages" -> {(n:NodeSeq) => pagesXml(0 until numPages, n)},
         "zoomedpages" -> {(ns: NodeSeq) => pagesXml(zoomedPages, ns)},
         "next" -> pageXml(first+itemsPerPage min itemsPerPage*(numPages-1) max 0, nextXml),
         "last" -> pageXml(itemsPerPage*(numPages-1), lastXml),
         "records" -> currentXml,
         "recordsFrom" -> Text(recordsFrom),
         "recordsTo" -> Text(recordsTo),
         "recordsCount" -> Text(count.toString),
         AttrBindParam("prevclass", prevClass, "class"),
         AttrBindParam("nextclass", nextClass, "class")
       )
  }
}