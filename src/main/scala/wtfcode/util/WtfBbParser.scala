package wtfcode.util

import scabb.{TagNode, BbAst, ExtendableBbParser}
import wtfcode.model.User
import net.liftweb.common.Full
import xml.Text

trait UserExtension extends ExtendableBbParser {

  case class UserTag(content: String) extends BbAst {
    override val toHtml = User.findByNickName(content) match {
      case Full(user) => <a href={user.link}><i class="icon-user"/>{user.nickName}</a>
      case _ => Text("[user]" + content + "[/user]")
    }
  }

  override def astExtensions = ({
    case TagNode("user", None, content) => UserTag(content.mkString)
  }: Extension) orElse super.astExtensions

}

object WtfBbParser extends ExtendableBbParser with UserExtension