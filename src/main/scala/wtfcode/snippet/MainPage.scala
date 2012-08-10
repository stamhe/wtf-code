package wtfcode.snippet

import net.liftweb.util.Helpers
import Helpers._
import wtfcode.model._

class MainPage {
  def howdy = {
    ".users" #> User.count &
    ".posts" #> Post.count &
    ".comments" #> Comment.count
  }
}
