package wtfcode.model

import net.liftweb.mapper._
import net.liftweb.common.{Full, Box}

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
    if (user.isEmpty || post.isEmpty)
      return 0

    val maybeSeen = LastSeen.find(By(LastSeen.user, user), By(LastSeen.post, post))
    val seenAt = maybeSeen.map(_.updatedAt).map(_.toLong).openOr(0L)
    post.open_!.comments.count(comment => comment.createdAt.toLong > seenAt && comment.author.foreign != user)
  }
}