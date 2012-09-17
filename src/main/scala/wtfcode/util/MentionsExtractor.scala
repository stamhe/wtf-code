package wtfcode.util

import scabb.{TagNode, BbAst, ExtendableBbParser}
import collection.mutable
import wtfcode.model.User
import xml.Text

trait MentionsExtractorExtension extends ExtendableBbParser {

  val mentions = new mutable.HashSet[User]

  case class UserTag(content: String) extends BbAst {
    override val toHtml = {
      User.findByNickName(content).map(mentions.add(_))
      Text("[user]" + content + "[/user]")
    }
  }

  override def astExtensions = ({
    case TagNode("user", None, content) => UserTag(content.mkString)
  }: Extension) orElse super.astExtensions

}

class MentionsExtractor(input: String) extends ExtendableBbParser with MentionsExtractorExtension {
  toHtml(input)
}
