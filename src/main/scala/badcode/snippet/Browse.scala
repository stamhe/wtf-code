package badcode.snippet

import xml.NodeSeq
import badcode.model.BadCode
import net.liftweb.util.Helpers
import Helpers._

class Browse {
  def howdy(in: NodeSeq): NodeSeq = {
    val codes = BadCode.findAll()
    codes.flatMap(code => Helpers.bind("entry", in,
      "content" -> code.content,
      "description" -> code.description,
      "author" -> code.author,
      "date" -> code.date))
  }
}
