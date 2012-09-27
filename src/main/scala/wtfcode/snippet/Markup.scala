package wtfcode.snippet

import net.liftweb.util.Helpers._
import wtfcode.util.WtfBbParser

class Markup {

  private val Examples = List(
    "[i]empased[/i] with italic",
    "a [b]bold[/b] [color=green]green[/color] troll",
    "[u]underlined[/u], [s]striked[/s] text",
    "lets [q]quote[/q] someone",
    "some [code]inline code[/code] here",
    "[code]multiline\ncode\nworks[/code]",
    "just [url]http://some.url[/url]",
    "link [url=http://google.com]to Google[/url]"
  )

  def examples = {
    ".example *" #> Examples.map(e =>
      ".source *" #> <pre><code>{e}</code></pre> & ".result *" #> WtfBbParser.toHtml(e)
    )
  }
}
