package wtfcode.atom

import wtfcode.model.Comment
import net.liftweb.mapper._
import net.liftweb.util.Helpers
import net.liftweb.textile.TextileParser

object CommentFeed extends AtomFeed[Comment] {

  val LIMIT = 20

  def entries = Comment.findAll(OrderBy(Comment.createdAt, Descending), MaxRows(LIMIT))

  def feedId = "urn:feed:comments:" + entries.headOption.map(_.id).map(_.get).getOrElse(0L)

  def feedUpdated = entries.headOption.map(_.createdAt).map(_.get).getOrElse(Helpers.now)

  def entryId(entry: Comment) = "urn:comment:" + entry.id.is

  def entryTitle(entry: Comment) = "#" + entry.id.is

  def entryUpdated(entry: Comment) = entry.createdAt.is

  def entryAuthorName(entry: Comment) = entry.author.foreign.map(_.nickName).map(_.is).openOr("Guest")

  def entryContent(entry: Comment) = TextileParser.toHtml(entry.content.is)
}
