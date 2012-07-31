package wtfcode.model

import net.liftweb.mapper._

/**
 * Represents set of users voted for the comment.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class CommentVote extends LongKeyedMapper[CommentVote] with IdPK {
  def getSingleton = CommentVote

  object comment extends MappedLongForeignKey(this, Comment) {
    override def dbIndexed_? = true
  }

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object score extends MappedInt(this)
}

object CommentVote extends CommentVote with LongKeyedMetaMapper[CommentVote] {
  override def dbTableName = "comment_votes"
}
