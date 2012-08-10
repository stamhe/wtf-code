package wtfcode.model

import net.liftweb.mapper._
import net.liftweb.common.{Full, Box}

/**
 * Be careful. You can't unsee this.
 */
class LastSeen extends LongKeyedMapper[LastSeen] with IdPK with UpdatedTrait {
  def getSingleton = LastSeen

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object post extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }
}

object LastSeen extends LastSeen with LongKeyedMetaMapper[LastSeen] {
  override def dbTableName = "last_seen"

  def update(user: Box[User], post: Box[Post]) {
    if (user.isEmpty || post.isEmpty)
      return

    val seen = LastSeen.find(By(LastSeen.user, user), By(LastSeen.post, post))
    seen match {
      case Full(x) => x.updatedAt(null).save() //XXX other way to force save?
      case _ => LastSeen.create.user(user).post(post).save()
    }
  }

  def unseenCount(user: Box[User], post: Box[Post]): Int = {
    (user, post) match {
      case (Full(user), Full(post)) => unseenCount(user, post)
      case _ => 0
    }
  }

  def unseenCount(user: User, post: Post): Int = {
    val seenAt = getSeenAt(user, post)
    post.comments.count(comment => seen(comment, user, seenAt))
  }

  def unseen(comment: Comment): Boolean = {
    (User.currentUser, comment.post.foreign) match {
      case (Full(user), Full(post)) => seen(comment, user, getSeenAt(user, post))
      case _ => false
    }
  }

  def getSeenAt(user: User, post: Post): Long = {
    val maybeSeen = LastSeen.find(By(LastSeen.user, user), By(LastSeen.post, post))
    maybeSeen.map(_.updatedAt).map(_.toLong).openOr(0L)
  }

  private def seen(comment: Comment, user: User, seenAt: Long): Boolean = {
    comment.createdAt.toLong > seenAt && comment.author.foreign != user
  }
}