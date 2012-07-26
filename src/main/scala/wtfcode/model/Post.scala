package wtfcode.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK with CreatedTrait {
  def getSingleton = Post

  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)

  def getComments : List[Comment] = Comment.findAll(By(Comment.code, this))
  def link: String = "/code/" + id
}

object Post extends Post with LongKeyedMetaMapper[Post] {
  override def dbTableName = "posts"
}