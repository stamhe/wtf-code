package wtfcode.model

import net.liftweb.mapper._

class Bookmark extends LongKeyedMapper[Bookmark] with IdPK with CreatedTrait {
  def getSingleton = Bookmark

  override val createdAtIndexed_? = true

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object post extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }
}

object Bookmark extends Bookmark with LongKeyedMetaMapper[Bookmark] {
  override def dbTableName = "bookmarks"
}