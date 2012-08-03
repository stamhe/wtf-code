package wtfcode.util

import net.liftweb.http.js.{JsMember, JsExp}

case class JqAddClass(clazz: JsExp) extends JsMember {
  override def toJsCmd = "addClass(" + clazz.toJsCmd + ")"
}

case class JqRemoveClass(clazz: JsExp) extends JsMember {
  override def toJsCmd = "removeClass(" + clazz.toJsCmd + ")"
}
