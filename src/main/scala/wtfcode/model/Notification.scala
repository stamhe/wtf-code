package wtfcode.model

import net.liftweb.mapper._

class Notification extends LongKeyedMapper[Notification] with IdPK with CreatedTrait {
  def getSingleton = Notification

  override val createdAtIndexed_? = true

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object read extends MappedBoolean(this)
}

object Notification extends Notification with LongKeyedMetaMapper[Notification] {
  override def dbTableName = "notifications"
}