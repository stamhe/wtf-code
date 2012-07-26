package wtfcode.model
import net.liftweb.mapper._

/**
 * Represents a programming language.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class Language extends LongKeyedMapper[Language] with IdPK with CreatedTrait with OneToMany[Long, Language] {

  def getSingleton = Language

  override def createdAtIndexed_? = true

  object name extends MappedText(this)

  object posts extends MappedOneToMany(Post, Post.language, OrderBy(Post.createdAt, Descending))
}

object Language extends Language with LongKeyedMetaMapper[Language] {
  override def dbTableName = "languages"
}


