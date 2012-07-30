package wtfcode.snippet
import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.util.Helpers
import Helpers._
import wtfcode.model.{Bookmark, Comment, User, Post}

class MainPage {
  def howdy(in: NodeSeq): NodeSeq = {
    Helpers.bind("b", in,
      "time" -> (new _root_.java.util.Date).toString,
      "codes" -> Post.count,
      "users" -> User.count,
      "comments" -> Comment.count,
      "bookmarks" -> Bookmark.count
    )
  }
}
