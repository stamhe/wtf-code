package badcode.model

import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedTrait {
  def getSingleton = Comment

  object author extends MappedLongForeignKey(this, User)
  object code extends MappedLongForeignKey(this, BadCode)
  object content extends MappedText(this)
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  override def dbTableName = "comments"
}