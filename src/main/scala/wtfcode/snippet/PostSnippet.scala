package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Post, User}

class PostSnippet {
  def post(xhtml: NodeSeq): NodeSeq = {
    var content = ""
    var description = ""

    def processPost () {
      val post = Post.create.author(User.currentUser).content(content).description(description)
      post.save()
      S.redirectTo(post.link)
    }

    Helpers.bind("entry", xhtml,
      "content" -> SHtml.textarea(content, content = _ , "cols" -> "80", "rows" -> "8"),
      "description" -> SHtml.textarea(description, description = _ , "cols" -> "80", "rows" -> "8"),
      "submit" -> SHtml.submit("Add", processPost))
  }
}
