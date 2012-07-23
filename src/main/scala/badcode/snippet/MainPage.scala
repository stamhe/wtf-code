package badcode {
package snippet {

import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.util.Helpers
import Helpers._
import model.{User, BadCode}

class MainPage {
  def howdy(in: NodeSeq): NodeSeq = {
    Helpers.bind("b", in,
      "time" -> (new _root_.java.util.Date).toString,
      "codes" -> BadCode.count,
      "users" -> User.count
    )
  }
}

}

}
