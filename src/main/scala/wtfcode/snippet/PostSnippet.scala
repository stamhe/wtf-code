package wtfcode.snippet

import xml.{Text, NodeSeq}
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Language, Post, User}
import net.liftweb.common.Empty
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.SetHtml
import wtfcode.util.{JqRemoveClass, JqAddClass, CodeBinder}
import net.liftweb.http.js.jquery.JqJE.JqId
import net.liftweb.http.js.JE.Str

class PostSnippet {
  def post(xhtml: NodeSeq): NodeSeq = {
    var content = ""
    var description = ""
    var langId: Long = 0

    def createPost(): Post = {
      val language = Language.find(langId)
      Post.create.author(User.currentUser).content(content).description(description).language(language)
    }

    def process(func: () => JsCmd): JsCmd = {
      clearErrors() &
      (content.trim.length match {
        case 0 => compilationError(S ? "post.codeNotFound")
        case _ => func()
      })
    }

    def processPost(): JsCmd = {
      val post = createPost()
      post.save

      val language = post.language.open_!
      language.postNumber(language.postNumber.is + 1)
      language.save()

      S.redirectTo(post.link)
    }

    def processPreview(): JsCmd = {
      val post = createPost()
      val template = S.runTemplate("templates-hidden" :: "code" :: Nil)
      SetHtml("preview", CodeBinder(template.open_!, post))
    }

    def compilationError(s: String): JsCmd = {
      SetHtml("content-inline-help", Text(S ? "post.compilationError" + ": " + s)) &
      (JqId("content-group") ~> JqAddClass(Str ("error"))).cmd
    }

    def clearErrors(): JsCmd = {
      SetHtml("content-inline-help", Text("")) &
      (JqId("content-group") ~> JqRemoveClass(Str ("error"))).cmd
    }

    val languages = Language.findAll().map(lang => (lang.id.toString, lang.name.toString))

    SHtml.ajaxForm(
      bind("entry", xhtml,
        "language" -> SHtml.select(languages, Empty, l => langId = l.toLong),
        "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
        "description" -> SHtml.textarea(description, description = _, "cols" -> "80", "rows" -> "8"),
        "submit" -> SHtml.ajaxSubmit(S ? "post.add", () => process(processPost), "class" -> "btn btn-primary"),
        "preview" -> SHtml.ajaxSubmit(S ? "post.preview", () => process(processPreview), "class" -> "btn btn-primary")))
  }
}
