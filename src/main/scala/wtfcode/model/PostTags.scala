package wtfcode.model
import net.liftweb.mapper._

class PostTags extends LongKeyedMapper[PostTags] with IdPK {
  def getSingleton = PostTags

  object post extends MappedLongForeignKey(this, Post) {
    override def dbIndexed_? = true
  }

  object tag extends MappedLongForeignKey(this, Tag) {
    override def dbIndexed_? = true
  }
}

object PostTags extends PostTags with LongKeyedMetaMapper[PostTags] {
  override def dbTableName = "post_tags"
}
