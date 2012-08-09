package wtfcode.model
import net.liftweb.mapper._

class Tag extends LongKeyedMapper[Tag] with IdPK {
  def getSingleton = Tag

  object value extends MappedString(this, 30) {
    override def dbIndexed_? = true
  }
}

object Tag extends Tag with LongKeyedMetaMapper[Tag] {
  override def dbTableName = "tags"
}
