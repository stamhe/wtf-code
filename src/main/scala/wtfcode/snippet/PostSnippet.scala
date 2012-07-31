package wtfcode.snippet

import xml.NodeSeq
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Language, Post, User}
import net.liftweb.common.Empty
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import wtfcode.util.CodeBinder

class PostSnippet {
  def post(xhtml: NodeSeq): NodeSeq = {
    var content = ""
    var description = ""
    var langId: Long = 0

    def createPost(): Post = {
      val lang = Language.find(langId).open_!
      val post = Post.create.author(User.currentUser).content(content).description(description).language(lang)
      lang.postNumber(lang.postNumber.is + 1)
      post.save
      lang.save()
      post
    }

    def processPost(): JsCmd = {
      val post = createPost()
      S.redirectTo(post.link)
    }

    def processPreview(): JsCmd = {
      val post = createPost()
      val template = S.runTemplate("templates-hidden" :: "code" :: Nil)
      SetHtml("preview", CodeBinder(template.open_!, post))
    }

    val languages = Language.findAll().map(lang => (lang.id.toString, lang.name.toString))

    SHtml.ajaxForm(
      bind("entry", xhtml,
        "language" -> SHtml.select(languages, Empty, l => langId = l.toLong),
        "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
        "description" -> SHtml.textarea(description, description = _, "cols" -> "80", "rows" -> "8"),
        "submit" -> SHtml.ajaxSubmit(S ? "post.add", () => processPost(), "class" -> "btn btn-primary"),
        "preview" -> SHtml.ajaxSubmit(S ? "post.preview", () => processPreview(), "class" -> "btn btn-primary")))
  }
}
