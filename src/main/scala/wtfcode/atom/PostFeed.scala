package wtfcode.atom

import wtfcode.model.Post
import net.liftweb.mapper._
import net.liftweb.util.Helpers
import net.liftweb.mapper.MaxRows

class PostFeed(val param: String) extends AtomFeed[Post] {

  def count = Post.count

  def path = "posts"

  def entries = Post.findAll(
    OrderBy(Post.createdAt, Descending),
    StartAt(curPage * itemsPerPage),
    MaxRows(itemsPerPage))

  def feedTitle = "WtfCode"

  def feedId = "urn:feed:posts:" + entries.headOption.map(_.id).map(_.get).getOrElse(0L)

  def feedUpdated = entries.headOption.map(_.createdAt).map(_.get).getOrElse(Helpers.now)

  def entryId(entry: Post) = "urn:post:" + entry.id.is

  def entryDeleted(entry: Post) = false

  def entryDeletedAt(entry: Post) = null

  def entryTitle(entry: Post) = "#" + entry.id.is

  def entryUpdated(entry: Post) = entry.createdAt.is

  def entryAuthorName(entry: Post) = entry.author.foreign.map(_.nickName).map(_.is).openOr("Guest")

  def entryContent(entry: Post) = <pre><code>{entry.content.is}</code></pre>
}
