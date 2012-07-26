package wtfcode.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedTrait {
  def getSingleton = Comment

  override def createdAtIndexed_? = true

  object author extends MappedLongForeignKey(this, User)
  object code extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }
  object content extends MappedText(this)

  def anchor: String = "comment_" + id
  def link: String = "#" + anchor
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  override def dbTableName = "comments"
}