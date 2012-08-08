package wtfcode.snippet

import xml.NodeSeq
import wtfcode.model.{User, Notification}
import net.liftweb.util.Helpers._
import net.liftweb.http.PaginatorSnippet

class Notifications extends PaginatorSnippet[Notification] {
  val startAt = curPage * itemsPerPage

  override def itemsPerPage = 20
  override def count = User.currentUser.map(_.notifications.size.toLong).openOr(0)
  override def page = User.currentUser.map(_.notifications.slice(startAt, startAt + itemsPerPage)).openOr(Nil)

  def renderPage =
    ".notifications *" #> ((in: NodeSeq) => page.flatMap {notification => bindNotification(notification)(in)})

  def bindNotification(notification: Notification): (NodeSeq => NodeSeq) = {
    ".date *" #> notification.createdAt
  }
}
