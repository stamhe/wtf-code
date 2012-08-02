package wtfcode.util

import xml.NodeSeq
import wtfcode.model.{Bookmark, User, Post}
import net.liftweb.util.Helpers._
import net.liftweb.textile.TextileParser
import net.liftweb.http.{S, SHtml}
import net.liftweb.mapper.By
import net.liftweb.http.js.jquery.JqJsCmds.Hide
import net.liftweb.common.Full
import net.liftweb.http.js.JsCmds.SetHtml

object CodeBinder {

  def apply(template: NodeSeq, post: Post): NodeSeq = {
    bind("entry", template,
      "id" -> post.id,
      "language" -> post.getLanguage,
      "content" -> post.content,
      "description" -> TextileParser.toHtml(post.description),
      "author" -> post.author.map(_.nickName.get).openOr("Guest"),
      "date" -> post.createdAt,
      "commentsNum" -> post.comments.size,
      "bookmark" -> bookmarkAction(post),
      "rate" -> RateBinder(S.runTemplate(List("templates-hidden", "rating")).open_!, post),
      AttrBindParam("link_to_author", post.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_code", post.link, "href"),
      AttrBindParam("lang_code", post.language.obj.map {_.code.is} openOr "", "class"))
  }

  private def mkAddBookmarkItem(id: String) = <i class="icon-star" id={id} title={S ? "bookmark.add"}></i>
  private def mkRemBookmarkItem(id: String) = <i class="icon-star-empty" id={id} title={S ? "bookmark.remove"}></i>

  private def bookmarkAction(post: Post) = {
    val id = "bookmark_" + post.id

    User.currentUser map { user =>
      val maybeBookmark = Bookmark.find(By(Bookmark.user, user), By(Bookmark.post, post))
      maybeBookmark match {
        case Full(bookmark) => SHtml.a(() => {
          bookmark.delete_!
          Hide(id)
        }, mkRemBookmarkItem(id))
        case _ => SHtml.a(() => {
          Bookmark.create.user(user).post(post).save()
          Hide(id)
        }, mkAddBookmarkItem(id))
      }
    }
  }
}
