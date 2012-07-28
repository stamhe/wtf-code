package wtfcode.model

import net.liftweb.mapper._

class Bookmark extends LongKeyedMapper[Bookmark] with IdPK with CreatedTrait {
  def getSingleton = Bookmark

  object user extends MappedLongForeignKey(this, User)
  object post extends MappedLongForeignKey(this, Post)
}

object Bookmark extends Bookmark with LongKeyedMetaMapper[Bookmark] {
  override def dbTableName = "bookmarks"
}