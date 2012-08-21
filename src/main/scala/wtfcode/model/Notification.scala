package wtfcode.model

import net.liftweb.mapper._

class Notification extends LongKeyedMapper[Notification] with IdPK with CreatedTrait {
  def getSingleton = Notification

  override val createdAtIndexed_? = true

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object from extends MappedLongForeignKey(this, User)
  object read extends MappedBoolean(this)
  object link extends MappedText(this)
}

object Notification extends Notification with LongKeyedMetaMapper[Notification] {
  override def dbTableName = "notifications"

  def newComment(newComment: Comment) {
    //new comment to post
    newComment.post.map { post =>
      post.author.map { author =>
        Notification.create.user(author).from(newComment.author).link(newComment.link).save()
    }}

    //new response to comment
    newComment.responseTo.map { to =>
      to.author.map { author =>
        Notification.create.user(author).from(newComment.author).link(newComment.link).save()
    }}
  }

  def deletedComment(comment: Comment) {
    comment.author.map { author =>
      Notification.create.user(author).from(User.currentUser).link(comment.link).save()
    }
  }

  def deletedPost(post: Post) {
    post.author.map { author =>
      Notification.create.user(author).from(User.currentUser).link(post.link).save()
    }
  }
}