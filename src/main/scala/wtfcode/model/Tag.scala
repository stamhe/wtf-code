package wtfcode.model
import net.liftweb.mapper._

class Tag extends ProtoTag[Tag] {
  def getSingleton = Tag
  def cacheSize = 100

  def complete(input: String, limit: Int): List[String] = {
    // TODO(Roman): check why Like query don't work
    // val found = Tag.findAllFields(Seq(Tag.name), Like(Tag.name, input + "%"), MaxRows(limit)).map(_.name.is)
    val found = Tag.findAllFields(Seq(Tag.name)).map(_.name.is).filter(_ startsWith input).take(limit)
    if (found.isEmpty) List(input) else found
  }
}

object Tag extends Tag with MetaProtoTag[Tag] {
  override def dbTableName = "tags"
}
