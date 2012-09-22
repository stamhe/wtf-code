package wtfcode {
package util {

import xml.{Text, NodeSeq}
import model._
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import net.liftweb.mapper.By
import net.liftweb.http.js.jquery.JqJsCmds.{AppendHtml, Hide}
import net.liftweb.common.Full
import net.liftweb.util.CssSel
import net.liftweb.http.js.JsCmds


object CodeBinders {

  trait BindStrategy {
    def apply(post: Post): CssSel
  }

  class Defaults extends BindStrategy {
    override def apply(post: Post) = {
      val langObj = post.language.obj
      val unseenCount = LastSeen.unseenCount(User.currentUser, Full(post))

      ".entry-id *" #> post.id &
        ".language *" #> post.getLanguage &
        ".content *" #> post.content &
        ".content [class]" #> post.language.map(_.htmlClass.get).openOr("") &
        ".description *" #> WtfBbParser.toHtml(post.description) &
        ".link-to-author *" #> post.author.map(_.nickName.get).openOr("Guest") &
        ".date *" #> post.createdAt &
        ".comments-num *" #> post.comments.size &
        (if (unseenCount > 0) ".newCommentsNum *" #> ("+" + unseenCount)
        else ".new-comments-num" #> (None: Option[NodeSeq])) &
        ".avatar [src]" #> Avatar(post.author, Full(post.ipAddress)) &
        ".link-to-author [href]" #> post.author.map(_.link).openOr("#") &
        ".link-to-code [href]" #> post.link &
        ".link-to-lang-filter [href]" #> langObj.map(_.link).openOr("#") &
        ".lang_code [href]" #> langObj.map(_.code.is).openOr("")
    }
  }

  trait Rating extends Defaults {
    lazy val RatingTemplate = S.runTemplate(List("templates-hidden", "rating"))
      .openOrThrowException("Rating template doesn't exist")

    override def apply(post: Post) = {
      super.apply(post) & ".post-rating *" #> RateBinder(post)(RatingTemplate)
    }
  }

  trait Tags extends Defaults {
    override def apply(post: Post) = {
      val tags = Post.tags
      super.apply(post) & (if (tags.isEmpty) ".tags" #> NodeSeq.Empty
      else ".tags *" #> ((in: NodeSeq) => tags flatMap {
        t => (".tag *" #> t.name.is).apply(in)
      }))
    }
  }

  trait AjaxComments extends Defaults {

    override def apply(post: Post) = {
      val commentsDivId = "comments_" + post.id.is
      super.apply(post) &
        ".comments [id]" #> commentsDivId &
        ".comments-link *" #> SHtml.a(() => {
          lazy val comments = CommentBinders.applyToRoots(RecursiveCommentBinder, post)
          AppendHtml(commentsDivId, comments)
        }, <i class="icon-comment"/> <span>
          {S ? "post.comments"}
        </span>)
    }
  }

  trait Bookmarks extends Defaults {

    override def apply(post: Post) = {
      super.apply(post) & ".bookmark *" #> bookmarkAction(post)
    }

    private def mkAddBookmarkItem(id: String) = <i class="icon-star-empty" id={id} title={S ? "bookmark.add"}></i>

    private def mkRemBookmarkItem(id: String) = <i class="icon-star" id={id} title={S ? "bookmark.remove"}></i>

    private def bookmarkAction(post: Post) = {
      val id = "bookmark_" + post.id

      User.currentUser map {
        user =>
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

  trait PostDelete extends Defaults {
    override def apply(post: Post) = {
      super.apply(post) &
        (if (post.canDelete) "#delete-post" #> SHtml.a(() => {
          post.delete(); JsCmds.Noop
        }, Text("!DELETE!"))
        else "#delete-post" #> NodeSeq.Empty)
    }
  }

}

import CodeBinders._

class DefaultBinder extends Defaults with Rating with Bookmarks with Tags with PostDelete

object CodeBinder extends DefaultBinder

object AjaxCommentsCodeBinder extends DefaultBinder with AjaxComments

}
}