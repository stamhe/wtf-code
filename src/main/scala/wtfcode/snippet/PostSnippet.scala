package wtfcode.snippet

import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Tag, Language, Post, User}
import net.liftweb.common.{Box, Empty}
import net.liftweb.http.js.JsCmd
import wtfcode.util._
import net.liftweb.http.js.JE.Str
import wtfcode.util.JqAddClass
import wtfcode.util.JqRemoveClass
import net.liftweb.http.js.jquery.JqJE.JqId
import xml.Text
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftmodules.widgets.autocomplete.AutoComplete
import net.liftweb.mapper.Like

class PostSnippet {
  def post() = {
    var content = ""
    var description = ""
    var langId: Long = 0
    var tags = Set()
    var language: Box[Language] = Empty

    def createPost(): Post = {
      Post.create.author(User.currentUser).content(content).description(description).language(language)
    }

    def process(func: () => JsCmd): JsCmd = {
      language = Language.find(langId)

      val cmd : JsCmd = if (content.trim.length < 1) {
        compilationError(S ? "post.codeNotFound")
      } else if (language.isEmpty) {
        compilationError(S ? "post.langNotSelected")
      } else {
        func()
      }
      clearErrors() & ReCaptcha.reloadCaptcha() & cmd
    }

    def processPost(): JsCmd = {
      val captchaErrors = ReCaptcha.validateCaptcha()
      if (!captchaErrors.isEmpty) {
        compilationError(S ? "post.wrongCaptchaAnswer" + ": " + captchaErrors.mkString("\n"))
      } else {
        val post = createPost().saveMe()

        post.language map { language =>
          language.postNumber(language.postNumber.is + 1)
          language.save()
        }

        S.redirectTo(post.link)
      }
    }

    def processPreview(): JsCmd = {
      val post = createPost()
      val template = S.runTemplate("templates-hidden" :: "code" :: Nil).openOrThrowException("template must exist")
      SetHtml("preview", CodeBinder(post)(template)) & SyntaxHighlighter.highlightPage()
    }

    def compilationError(s: String): JsCmd = {
      SetHtml("content-inline-help", Text(S ? "post.compilationError" + ": " + s)) &
      (JqId("content-group") ~> JqAddClass(Str ("error"))).cmd
    }

    def clearErrors(): JsCmd = {
      SetHtml("content-inline-help", Text("")) &
      (JqId("content-group") ~> JqRemoveClass(Str ("error"))).cmd
    }

    val languages = Language.orderedByPopularity().map(lang => (lang.id.toString, lang.name.toString))

    ".language" #> SHtml.select(languages, Empty, l => langId = l.toLong) &
      ".content" #> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8") &
      ".description" #> SHtml.textarea(description, description = _, "cols" -> "80", "rows" -> "8") &
      "#reCaptcha *" #> ReCaptcha.captchaXhtml() &
      ".tags-input *" #> AutoComplete("", (input, limit) => Tag.complete(input, limit), value => tags += value) &
      ".submit" #> SHtml.ajaxSubmit(S ? "post.add", () => process(processPost), "class" -> "btn btn-primary") &
      ".preview" #> SHtml.ajaxSubmit(S ? "post.preview", () => process(processPreview), "class" -> "btn btn-primary")
  }
}
