package wtfcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model._

class MainPage {
  def howdy = {
    ".time" #> (new _root_.java.util.Date).toString &
    ".codes" #> Post.count &
    ".users" #> User.count &
    ".comments" #> Comment.count &
    ".bookmarks" #> Bookmark.count &
    ".last_seen" #> LastSeen.count &
    ".notifications" #> Notification.count
  }
}
