package wtfcode.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK
with CreatedTrait with SaveIP with OneToMany[Long, Post] with ManyToMany {
  def getSingleton = Post

  object language extends MappedLongForeignKey(this, Language) {
    override def dbIndexed_? = true
  }
  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)
  object rating extends MappedInt(this) {
    override def defaultValue = 0
  }

  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Ascending))
  object votes extends MappedManyToMany(PostVote, PostVote.post, PostVote.user, User)

  def getLanguage = this.language.obj.map(_.name.toString).openOr("None")

  def canVote(user: User) =
    author != user &&
    PostVote.count(By(PostVote.post, this), By(PostVote.user, user)) == 0

  private def updateVotes(user: User, func: Int => Int): Int = {
    PostVote.create.post(this).user(user).save()
    rating(func(rating))
    save
    rating
  }

  def voteOn(user: User): Int = updateVotes(user, _ + 1)

  def voteAgainst(user: User): Int = updateVotes(user, _ - 1)

  def link: String = "/code/" + id
}

object Post extends Post with LongKeyedMetaMapper[Post] {
  override def dbTableName = "posts"
}