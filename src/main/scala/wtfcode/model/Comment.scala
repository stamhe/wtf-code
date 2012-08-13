package wtfcode.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedTrait
with SaveIP with Rated with OneToMany[Long, Comment] with ManyToMany {
  def getSingleton = Comment

  override val createdAtIndexed_? = true

  object author extends MappedLongForeignKey(this, User)
  object post extends MappedLongForeignKey(this, Post)
  object content extends MappedText(this)
  object responseTo extends MappedLongForeignKey(this, Comment) {
    override def dbIndexed_? = true
  }

  object rating extends MappedInt(this) {
    override def defaultValue = 0
  }

  object deleted extends MappedBoolean(this)

  object votes extends MappedManyToMany(CommentVote, CommentVote.comment, CommentVote.user, User)

  object answers extends MappedOneToMany(Comment, Comment.responseTo, OrderBy(Comment.createdAt, Ascending))

  def anchor = "comment_" + id
  def repliesAnchor = "replies_" + id
  def link = post.foreign.map(_.link).openOr("") + "#" + anchor

  override def currentRating = this.rating.is
  override def canVote(user: User) =
    author != user &&
      CommentVote.count(By(CommentVote.comment, this), By(CommentVote.user, user)) == 0

  override protected def updateVotes(user: User, func: Int => Int): Int = {
    CommentVote.create.comment(this).user(user).save()
    rating(func(rating))
    save
    rating
  }

  def canDelete: Boolean = {
    User.currentUser.map(_.superUser.is).openOr(false)
  }
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  val MinRating = -5

  override def dbTableName = "comments"
}
