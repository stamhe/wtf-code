package wtfcode.model

import net.liftweb.mapper._

/**
 * Represents set of users voted for the post.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class PostVote extends LongKeyedMapper[PostVote] with IdPK {
  def getSingleton = PostVote

  object post extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }

  object user extends MappedLongForeignKey(this, User)
  object score extends MappedInt(this)
}

object PostVote extends PostVote with LongKeyedMetaMapper[PostVote] {
  override def dbTableName = "post_votes"
}
