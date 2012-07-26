package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Language, Post, User}
import net.liftweb.common.Empty

class PostSnippet {
  def post(xhtml: NodeSeq): NodeSeq = {
    var content = ""
    var description = ""
    var langId: Long = 0

    def processPost() {
      Post.create.author(User.currentUser).content(content)
        .description(description).language(Language.find(langId))
        .save()
    }

    val languages = Language.findAll().map(lang => (lang.id.toString, lang.name.toString))

    Helpers.bind("entry", xhtml,
      "language" -> SHtml.select(languages, Empty, l => langId = l.toLong),
      "content" -> SHtml.textarea(content, content = _ , "cols" -> "80", "rows" -> "8"),
      "description" -> SHtml.textarea(description, description = _ , "cols" -> "80", "rows" -> "8"),
      "submit" -> SHtml.submit("Add", processPost))
  }
}
