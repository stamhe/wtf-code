package badcode.snippet

import xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers
import Helpers._
import badcode.model.BadCode
import java.util.Date

class Post {
  def post(xhtml: NodeSeq): NodeSeq = {
    var content = ""
    var description = ""

    def processPost () {
      //TODO author
      BadCode.create.content(content).description(description).date(new Date).save()
    }

    Helpers.bind("entry", xhtml,
      "content" -> SHtml.textarea(content, content = _ , "cols" -> "80", "rows" -> "8"),
      "description" -> SHtml.textarea(description, description = _ , "cols" -> "80", "rows" -> "8"),
      "submit" -> SHtml.submit("Add", processPost))
  }
}
