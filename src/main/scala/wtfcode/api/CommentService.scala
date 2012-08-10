package wtfcode.api

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST._
import net.liftweb.util.Helpers._
import wtfcode.model.{Post, Comment}

object CommentService extends RestHelper {

  serve {
    case Req("post" :: AsLong(id) :: "comments" :: Nil, "json", GetRequest) => {

      val postId = id.toLong
      val comments = wtfcode.model.Post.findByKey(postId).map { _.comments.map(c => toJson(c)) }
      comments.map(cs => JsonResponse(JArray(cs.toList))) ?~ "Post not found"
    }
  }

  def toJson(c: Comment) : JValue = {
    import net.liftweb.json.JsonDSL._

    ("id" -> c.id.toString) ~
    ("date" -> restTimestamp(c.createdAt.is)) ~
    ("author" -> c.author.map { _.nickName.is }.openOr("Guest") ) ~
    ("content" -> c.content.is) ~
    ("responseTo" -> c.responseTo.map{ _.id.toString }.openOr(null))
  }

  private def restTimestamp(date: java.util.Date) = {
    val f = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    f.format(date)
  }
}
