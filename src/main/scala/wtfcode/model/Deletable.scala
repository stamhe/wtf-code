package wtfcode.model

import net.liftweb.mapper.{BaseMapper, MappedDateTime, MappedBoolean}
import net.liftweb.util.Helpers

trait Deletable {
  self: BaseMapper =>

  object deleted extends MappedBoolean[MapperType](this.asInstanceOf[MapperType])
  object deletedAt extends MappedDateTime[MapperType](this.asInstanceOf[MapperType])

  def canDelete: Boolean = {
    val deletable = !deleted
    val hasPermission = User.currentUser.map(_.superUser.is).openOr(false)
    deletable && hasPermission
  }

  def delete() {
    deleted(true)
    deletedAt(Helpers.now)
    save
    onDelete()
  }

  protected def onDelete() {}
}
