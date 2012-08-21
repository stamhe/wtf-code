package wtfcode.snippet

import scala.xml._
import net.liftweb.http.PaginatorSnippet

trait BootstrapPaginatorSnippet[T] extends PaginatorSnippet[T] {

  override def pageXml(newFirst: Long, ns: NodeSeq): NodeSeq =
    if (first == newFirst || newFirst < 0 || newFirst >= count)
      <a href="#">{ ns }</a>
    else
      <a href={ pageUrl(newFirst) }>{ ns }</a>
}