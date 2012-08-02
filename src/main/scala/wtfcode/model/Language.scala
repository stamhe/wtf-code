package wtfcode.model
import net.liftweb.mapper._

/**
 * Represents a programming language.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
class Language extends LongKeyedMapper[Language] with IdPK with CreatedTrait with OneToMany[Long, Language] {

  def getSingleton = Language

  object name extends MappedText(this)

  object code extends MappedText(this)

  object posts extends MappedOneToMany(Post, Post.language, OrderBy(Post.createdAt, Descending))


  object postNumber extends MappedLong(this) {
    override def defaultValue = 0L
  }

  def link = "/lang/"  + this.code.is

  def orderedByPopularity() = Language.findAll(OrderBy(postNumber, Descending))
}

object Language extends Language with LongKeyedMetaMapper[Language] {
  override def dbTableName = "languages"

  def mangleName(name: String) = name.toLowerCase.replace("+", "p").replace("#", "s")
}


