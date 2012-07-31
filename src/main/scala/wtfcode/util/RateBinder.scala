package wtfcode.util

import xml.{Text, NodeSeq}
import wtfcode.model.{Rated, User, Post}
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Full
import net.liftweb.util.Helpers._
import net.liftweb.mapper.IdPK

/**
 * Binds rated object to template.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
object RateBinder {

  def apply(xhtml: NodeSeq, model: Rated with IdPK): NodeSeq = {

    def voteId(obj: AnyRef, id: Long) =
      obj.getClass.getSimpleName + "_" + id.toString + "_rating_value"

    def applyVote(update: User => Int) = {
      val maybeUser = User.currentUser
      if(!maybeUser.isEmpty && model.canVote(maybeUser.open_!)) {
        update(maybeUser.open_!)
      }

      val template = S.runTemplate(List("templates-hidden", "rating")).open_!
      SetHtml(voteId(model, model.id.is), apply(template, model))
    }

    val maybeUser = User.currentUser
    maybeUser match {
        case Full(user) if model.canVote(user) => SHtml.ajaxForm(
          bind("entry", xhtml,
            "rating" -> model.currentRating,
            "voteOn" -> SHtml.a(() => applyVote(model.voteOn _), Text("++")),
            "voteAgainst" -> SHtml.a(() => applyVote(model.voteAgainst _), Text("--")),
            AttrBindParam("id", voteId(model, model.id.is), "id")
          )
        )
        case _ => bind("entry", xhtml,
          "rating" -> model.currentRating,
          "voteOn" -> Text(if (maybeUser.isDefined) "++" else ""),
          "voteAgainst" -> Text(if (maybeUser.isDefined) "--" else ""),
          AttrBindParam("id", voteId(model, model.id.is), "id")
        )
    }
  }
}
