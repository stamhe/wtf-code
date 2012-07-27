package wtfcode.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK with CreatedTrait with OneToMany[Long, Post] with ManyToMany {
  def getSingleton = Post

  object language extends MappedLongForeignKey(this, Language)
  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)
  object rating extends MappedInt(this) {
    override def defaultValue = 0
  }

  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Descending))
  object votes extends MappedManyToMany(PostVote, PostVote.post, PostVote.user, User)

  def getLanguage = this.language.obj.map(_.name.toString).openOr("None")

  // TODO (roman): Add validation
  def voteOn(user: User): Int = {
    rating(rating + 1)
    rating
  }

  def voteAgainst(user: User): Int = {
    rating(rating - 1)
    rating
  }

  def link: String = "/code/" + id
}

object Post extends Post with LongKeyedMetaMapper[Post] {
  override def dbTableName = "posts"
}