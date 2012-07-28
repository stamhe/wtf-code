package wtfcode.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedTrait with SaveIP {
  def getSingleton = Comment

  override val createdAtIndexed_? = true

  object author extends MappedLongForeignKey(this, User)
  object post extends MappedLongForeignKey(this, Post)
  object content extends MappedText(this)

  def anchor: String = "comment_" + id
  def link: String = "#" + anchor
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  override def dbTableName = "comments"
}