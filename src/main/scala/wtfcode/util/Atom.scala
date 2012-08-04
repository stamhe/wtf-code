package wtfcode.util

import net.liftweb.http.{GetRequest, Req, LiftRules}
import xml.Node
import wtfcode.model.Post
import net.liftweb.mapper._
import net.liftweb.util.Helpers
import java.util.Date
import java.text.SimpleDateFormat
import net.liftweb.mapper.MaxRows
import net.liftweb.http.AtomResponse
import net.liftweb.common.Full

object Atom {

  def dispatch: LiftRules.DispatchPF = {
    case Req(List("atom", "posts"), _, GetRequest) => () => Full(AtomResponse(postFeed()))
  }

  def postFeed(): Node = {
    val LIMIT = 20
    val entries = Post.getSingleton.findAll(OrderBy(Post.createdAt, Descending), MaxRows(LIMIT))
    val id = entries.headOption.map(_.id).map(_.get).getOrElse(0L)
    val updated = format(entries.headOption.map(_.createdAt).map(_.get).getOrElse(Helpers.now))

    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>WtfCode</title>
      <id>urn:feed:{id}</id>
      <updated>{updated}</updated>
      {entries.flatMap(postToAtom)}
    </feed>
  }

  private def postToAtom(post: Post): Node = {
    <entry>
      <id>urn:post:{post.id.is}</id>
      <title>{"#" + post.id.is}</title>
      <updated>{format(post.createdAt.is)}</updated>
      <author>
        <name>{post.author.foreign.map(_.nickName).openOr("Guest")}</name>
      </author>
      <content type="xhtml">
        <div xmlns="http://www.w3.org/1999/xhtml"><pre><code>{post.content.is}</code></pre></div>
      </content>
    </entry>
  }

  private def format(date: Date): String = AtomDate.format(date)
}
