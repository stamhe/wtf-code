package wtfcode.model

import net.liftweb.http.S
import net.liftweb.mapper.{MappedText, BaseMapper}
import net.liftweb.util.Props

trait SaveIP {
  self: BaseMapper =>

  val ipAddress: MappedText[MapperType] = new MyIPAddress(this)

  protected class MyIPAddress(obj: self.type) extends MappedText[MapperType](obj.asInstanceOf[MapperType]) {
    override def defaultValue =
      Props.productionMode match {
        case true => S.request.flatMap(_.header("X-Forwarded-For")).openOr("NULL")
        case false => S.containerRequest.map(_.remoteAddress).openOr("NULL")
      }
  }
}
