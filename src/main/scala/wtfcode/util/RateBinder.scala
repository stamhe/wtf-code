package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model.{User, Post}
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Full
import net.liftweb.util.Helpers._

/**
 * Binds rated object to template.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object RateBinder {

  def apply(xhtml: NodeSeq, post: Post): NodeSeq = {

    def postVoteId(id: Long) = "post_" + id.toString + "_rating_value"

    def applyVote(update: User => Int) = {
      val maybeUser = User.currentUser
      if(!maybeUser.isEmpty && post.canVote(maybeUser.open_!)) {
        update(maybeUser.open_!)
      }

      val template = S.runTemplate(List("templates-hidden", "rating")).open_!
      SetHtml(postVoteId(post.id.is), apply(template, post))
    }

    val maybeUser = User.currentUser
    maybeUser match {
        case Full(user) if post.canVote(user) => SHtml.ajaxForm(
          bind("entry", xhtml,
            "rating" -> post.rating,
            "voteOn" -> SHtml.a(() => applyVote(post.voteOn _), Text("++")),
            "voteAgainst" -> SHtml.a(() => applyVote(post.voteAgainst _), Text("--")),
            AttrBindParam("id", postVoteId(post.id.is), "id")
          )
        )
        case _ => bind("entry", xhtml,
          "rating" -> post.rating,
          "voteOn" -> Text(if (maybeUser.isDefined) "++" else ""),
          "voteAgainst" -> Text(if (maybeUser.isDefined) "--" else ""),
          AttrBindParam("id", postVoteId(post.id.is), "id")
        )
    }
  }
}
