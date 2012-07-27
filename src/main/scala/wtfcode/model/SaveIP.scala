package wtfcode.model

import net.liftweb.http.S
import net.liftweb.mapper.{MappedText, BaseMapper}

trait SaveIP {
  self: BaseMapper =>

  val ipAddress: MappedText[MapperType] = new MyIPAddress(this)

  protected class MyIPAddress(obj: self.type) extends MappedText[MapperType](obj.asInstanceOf[MapperType]) {
    override def defaultValue = S.containerRequest.map(_.remoteAddress).openOr("NULL")
  }
}
