package wtfcode.util

import scabb.{BbNode, TagNode, BbAst, ExtendableBbParser}
import wtfcode.model.User
import net.liftweb.common.Full
import xml.Text

object WtfBbParser extends ExtendableBbParser {

  case class UserTag(content: String) extends BbAst {
    override val toHtml = User.findByNickName(content) match {
      case Full(user) => <a href={user.link}><i class="icon-user"/>{user.nickName}</a>
      case _ => Text("[user]" + content + "[/user]")
    }
  }

  object UserTagExtension extends Extension {
    def isDefinedAt(node: BbNode) = node match {
      case TagNode("user", None, _) => true
      case _ => false
    }

    def apply(node: BbNode) = UserTag(node.asInstanceOf[TagNode].contents.mkString)
  }

  override def astExtensions = UserTagExtension

}
