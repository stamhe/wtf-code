package wtfcode.atom

import wtfcode.model.Comment
import net.liftweb.mapper._
import net.liftweb.util.Helpers
import net.liftmodules.textile.TextileParser

class CommentFeed(val param: String) extends AtomFeed[Comment] {

  def count = Comment.count

  def path = "comments"

  def entries = Comment.findAll(
    OrderBy(Comment.createdAt, Descending),
    StartAt(curPage * itemsPerPage),
    MaxRows(itemsPerPage))

  def feedId = "urn:feed:comments:" + entries.headOption.map(_.id).map(_.get).getOrElse(0L)

  def feedUpdated = entries.headOption.map(_.createdAt).map(_.get).getOrElse(Helpers.now)

  def entryId(entry: Comment) = "urn:comment:" + entry.id.is

  def entryDeleted(entry: Comment) = entry.deleted.is

  def entryDeletedAt(entry: Comment) = entry.deletedAt.is

  def entryTitle(entry: Comment) = "#" + entry.id.is

  def entryUpdated(entry: Comment) = entry.createdAt.is

  def entryAuthorName(entry: Comment) = entry.author.foreign.map(_.nickName).map(_.is).openOr("Guest")

  def entryContent(entry: Comment) = TextileParser.toHtml(entry.content.is)
}
