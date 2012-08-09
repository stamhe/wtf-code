package wtfcode.model
import net.liftweb.mapper._

class Tag extends ProtoTag[Tag] {
  def getSingleton = Tag
  def cacheSize = 100
}

object Tag extends Tag with MetaProtoTag[Tag] {
  override def dbTableName = "tags"
}
