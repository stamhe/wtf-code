package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model._
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import net.liftweb.mapper.By
import net.liftweb.http.js.jquery.JqJsCmds.Hide
import net.liftweb.common.Full
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds

object CodeBinder {

  def apply(post: Post): (NodeSeq => NodeSeq) = {
    val langObj = post.language.obj
    val unseenCount = LastSeen.unseenCount(User.currentUser, Full(post))
    val ratingTemplate = S.runTemplate(List("templates-hidden", "rating")).openOrThrowException("template must exist")
    ".entry-id *" #> post.id &
      ".language *" #> post.getLanguage &
      ".content *" #> post.content &
      ".content [class]" #> post.language.map(_.htmlClass.get).openOr("") &
      ".description *" #> WtfBbParser.toHtml(post.description) &
      ".link_to_author *" #> post.author.map(_.nickName.get).openOr("Guest") &
      ".date *" #> post.createdAt &
      renderTags(post) &
      ".commentsNum *" #> post.comments.size &
      (if (unseenCount > 0) ".newCommentsNum *" #> ("+" + unseenCount)
      else ".newCommentsNum" #> (None: Option[NodeSeq])) &
      ".bookmark *" #> bookmarkAction(post) &
      ".post-rating *" #> RateBinder(post)(ratingTemplate) &
      ".avatar [src]" #> Avatar(post.author, post.ipAddress) &
      ".link_to_author [href]" #> post.author.map {_.link}.openOr("#") &
      ".link_to_code [href]" #> post.link &
      ".link_to_lang_filter [href]" #> langObj.map {_.link}.openOr("#") &
      ".lang_code [href]" #> langObj.map {_.code.is}.openOr("") &
      bindDelete(post)
  }

  private def mkAddBookmarkItem(id: String) = <i class="icon-star-empty" id={id} title={S ? "bookmark.add"}></i>
  private def mkRemBookmarkItem(id: String) = <i class="icon-star" id={id} title={S ? "bookmark.remove"}></i>

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

  private def bindDelete(post: Post): CssSel = {
    if (post.canDelete)
      "#delete-post" #> SHtml.a(() => {post.delete(); JsCmds.Noop}, Text("!DELETE!"))
    else
      "#delete-post" #> NodeSeq.Empty
  }

  private def createTag(post: Post, name: String) = {
    val tag = Tag.findOrCreate(name)
    PostTags.create.post(post).tag(tag).save()
  }

  private def renderTags(post: Post) = {
    val tags = Post.tags
    if (tags.isEmpty) {
      ".tags" #> NodeSeq.Empty
    } else {
      ".tags *" #> ((in: NodeSeq) => tags flatMap { t => (".tag *" #> t.name.is).apply(in) })
    }
  }
}
