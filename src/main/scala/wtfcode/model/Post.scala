package wtfcode.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK with CreatedTrait with OneToMany[Long, Post] {
  def getSingleton = Post

  object language extends MappedLongForeignKey(this, Language)
  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)

  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Descending))
}

object Post extends Post with LongKeyedMetaMapper[Post] {
  override def dbTableName = "posts"
}