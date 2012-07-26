package wtfcode.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedTrait {
  def getSingleton = Comment

  override val createdAtIndexed_? = true

  object author extends MappedLongForeignKey(this, User)
  object post extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }
  object content extends MappedText(this)
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  override def dbTableName = "comments"
}