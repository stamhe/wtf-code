package wtfcode.util

import xml.NodeSeq
import wtfcode.model.{Bookmark, User, Post}
import net.liftweb.util.Helpers._
import net.liftweb.textile.TextileParser
import net.liftweb.http.{S, SHtml}
import net.liftweb.mapper.By
import net.liftweb.http.js.jquery.JqJsCmds.Hide

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
      AttrBindParam("link_to_author", post.author.map(_.link).openOr("#"), "href"),
      AttrBindParam("link_to_code", post.link, "href"))
  }

  private def bookmarkAction(post: Post) = {
    val id = "bookmark_" + post.id
    User.currentUser map {
      user =>
        val maybeBookmark = Bookmark.find(By(Bookmark.user, user), By(Bookmark.post, post))
        maybeBookmark map {
          bookmark =>
            SHtml.a(() => {
              bookmark.delete_!
              Hide(id)
            }, <i class="icon-star" id={id} title={S ? "Remove from bookmarks"}></i>)
        } getOrElse {
          SHtml.a(() => {
            Bookmark.create.user(user).post(post).save()
            Hide(id)
          }, <i class="icon-star-empty" id={id} title={S ? "Add to bookmarks"}></i>)
        }
    }
  }
}
