package wtfcode.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK with CreatedTrait with OneToMany[Long, Post] {
  def getSingleton = Post

  object language extends MappedLongForeignKey(this, Language)
  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)
  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Descending))

  def getLanguage = this.language.obj.map(_.name.toString).openOr("None")

  def link: String = "/code/" + id
}

object Post extends Post with LongKeyedMetaMapper[Post] {
  override def dbTableName = "posts"
}