package badcode.model

import net.liftweb.mapper._

class BadCode extends LongKeyedMapper[BadCode] with IdPK with CreatedTrait {
  def getSingleton = BadCode

  object author extends MappedLongForeignKey(this, User)
  object content extends MappedText(this)
  object description extends MappedText(this)

  def getComments : List[Comment] = Comment.findAll(By(Comment.code, this))
}

object BadCode extends BadCode with LongKeyedMetaMapper[BadCode] {
  override def dbTableName = "badcodes"
}