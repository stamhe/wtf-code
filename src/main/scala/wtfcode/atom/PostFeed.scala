package wtfcode.atom

import wtfcode.model.Post
import net.liftweb.mapper._
import net.liftweb.util.Helpers
import net.liftweb.mapper.MaxRows

object PostFeed extends AtomFeed[Post] {

  val LIMIT = 20

  def entries = Post.findAll(OrderBy(Post.createdAt, Descending), MaxRows(LIMIT))

  def feedId = "urn:feed:posts:" + entries.headOption.map(_.id).map(_.get).getOrElse(0L)

  def feedUpdated = entries.headOption.map(_.createdAt).map(_.get).getOrElse(Helpers.now)

  def entryId(entry: Post) = "urn:post:" + entry.id.is

  def entryTitle(entry: Post) = "#" + entry.id.is

  def entryUpdated(entry: Post) = entry.createdAt.is

  def entryAuthorName(entry: Post) = entry.author.foreign.map(_.nickName).map(_.is).openOr("Guest")

  def entryContent(entry: Post) = <pre><code>{entry.content.is}</code></pre>
}
