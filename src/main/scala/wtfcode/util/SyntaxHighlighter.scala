package wtfcode.util

import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsRaw

/**
 * Object that incapsulates logic related to code javascript highlighting.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object SyntaxHighlighter {

  def highlightBlock(parentId : String) : JsCmd = JsRaw("wtfCode_highlightBlock(" + parentId + ")").cmd

  def highlightPage() : JsCmd = JsRaw("wtfCode_highlightPage()").cmd
}
