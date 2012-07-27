package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{User, Comment, Post}
import net.liftweb.util.Helpers
import Helpers._
import net.liftweb.http.{SHtml, S}
import net.liftweb.common.Empty
import wtfcode.util.RoboHash
import net.liftweb.textile.TextileParser
import net.liftweb.http.js.JsCmds.SetHtml

class ViewCode {
  val id = S.param("id") openOr ""

  val code = try {
    Post.findByKey(id.toLong)
  } catch {
    case e: NumberFormatException => Empty
  }

  def view(in: NodeSeq): NodeSeq = {
    code map ({
      i => bind("entry", in,
        "id" -> i.id,
        "language" -> i.getLanguage,
        "content" -> i.content,
        "description" -> TextileParser.toHtml(i.description),
        "author" -> i.author,
        "date" -> i.createdAt,
        AttrBindParam("link_to_author", i.author.map(_.link).openOr("#"), "href"),
        AttrBindParam("link_to_code", i.link, "href"))
    }) openOr Text("Not found")
  }

  def vote(in: NodeSeq): NodeSeq = {
    val user = User.currentUser openOr null
    val post = code.open_!
    SHtml.ajaxForm(
      bind("vote", in,
        "rating" -> code.open_!.rating,
        "voteOn" -> SHtml.ajaxButton(Text("++"), () => applyVote(post.voteOn(user))),
        "voteAgainst" -> SHtml.ajaxButton(Text("--"), () => applyVote(post.voteAgainst(user)))
      )
    )
  }

  def applyVote(newValue: Int) = SetHtml("post-rating-value", Text(newValue.toString))

  def comments(in: NodeSeq): NodeSeq = {
    code.open_!.comments.flatMap(
      comment => bind("entry", in,
        "content" -> TextileParser.toHtml(comment.content),
        "author" -> comment.author.map(_.nickName.toString).openOr("Guest"),
        "date" -> comment.createdAt,
        AttrBindParam("avatar_url", RoboHash.fromIp(comment.ipAddress), "src"),
        AttrBindParam("link_to_author", comment.author.map(_.link).openOr("#"), "href"),
        AttrBindParam("link_to_comment", comment.link, "href"),
        AttrBindParam("anchor", comment.anchor, "id"))
    )
  }

  def addComment(in: NodeSeq): NodeSeq = {
    var content = ""

    def processAddComment() {
      Comment.create.author(User.currentUser).post(code).content(content).save()
    }

    bind("entry", in,
      "content" -> SHtml.textarea(content, content = _, "cols" -> "80", "rows" -> "8"),
      "submit" -> SHtml.submit("Add", processAddComment))
  }
}
