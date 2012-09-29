package wtfcode.snippet

import xml.{Text, NodeSeq}
import wtfcode.model.{NotificationKind, User, Notification}
import net.liftweb.util.Helpers._
import net.liftweb.http.S

class Notifications extends BootstrapPaginatorSnippet[Notification] {
  val startAt = curPage * itemsPerPage

  override def itemsPerPage = 20
  override def count = User.currentUser.map(_.notifications.size.toLong).openOr(0)
  override def page = User.currentUser.map(_.notifications.slice(startAt, startAt + itemsPerPage)).openOr(Nil)

  def renderPage =
    ".notifications *" #> ((in: NodeSeq) => page.flatMap {notification => bindNotification(notification)(in)})

  def bindNotification(notification: Notification): (NodeSeq => NodeSeq) = {
    ".read [class]" #> read(notification) &
    ".link [href]" #> notification.link &
    ".preview *" #> preview(notification) &
    ".from *" #> notification.from.map(_.nickName.get).openOr("Guest") &
    ".from [href]" #> notification.from.map(_.link).openOr("#") &
    ".date *" #> notification.createdAt
  }

  private def read(notification: Notification): Option[String] =
    if (!notification.read) Some("unseen") else None

  private def preview(notification: Notification) =
    if (notification.kind.is == NotificationKind.Deletion)
      <span style="color: red;">{S ? "moderator.deletedBy"}</span>
    else
      Text(notification.preview.is)

  def updateRead() = {
    page.map(_.read(true).save())
    NodeSeq.Empty
  }
}
