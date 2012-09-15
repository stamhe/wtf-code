package wtfcode.api

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._
import net.liftweb.util.Helpers._
import wtfcode.util.WtfDateTimeConverter.formatTimestamp
import wtfcode.model.{Comment, Post => P}

object CommentService extends RestHelper {

  serve {
    case Req("post" :: AsLong(id) :: "comments" :: Nil, "json", GetRequest) => {
      val comments = P.findByKey(id).map { _.activeComments.map(c => toJson(c)) }
      comments.map(cs => JsonResponse(JArray(cs.toList))) ?~ "Post not found"
    }
  }

  def toJson(c: Comment) : JValue = {
    import net.liftweb.json.JsonDSL._

    ("id" -> c.id.toString) ~
      ("date" -> formatTimestamp(c.createdAt.is)) ~
      ("author" -> c.author.map { _.nickName.is }.openOr("Guest") ) ~
      ("content" -> c.content.is) ~
      ("responseTo" -> c.responseTo.map{ _.id.toString }.openOr(null))
  }
}
