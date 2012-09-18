package wtfcode.api

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST.{JValue, JArray}
import net.liftweb.util.Helpers._
import wtfcode.util.WtfDateTimeConverter.formatTimestamp
import wtfcode.model.{User, Post => P}
import net.liftweb.mapper.{OrderBy, Descending, By}

object CodeService extends RestHelper {

  serve {
    case Req("post" :: "by-id" :: AsLong(id) :: Nil, "json", GetRequest) => {
      val post = wtfcode.model.Post.findByKey(id)
      post.map(p => JsonResponse(toJson(p))) ?~ "Post not found"
    }
    case Req("post" :: "by-author" :: AsLong(id) :: Nil, "json", GetRequest) => {
      User.findByKey(id).map { user =>
        val posts = P.findAll(
          By(P.deleted, false),
          By(P.author, user),
          OrderBy(P.createdAt, Descending)
        )
        JsonResponse(JArray(posts.map(toJson)))
      } ?~ "User not found"
    }

  }

  def toJson(p: P): JValue = {
    import net.liftweb.json.JsonDSL._

    ("id" -> p.id.is.toString) ~
      ("language" -> p.language.obj.map(_.name.is).openOr("")) ~
      ("date" -> formatTimestamp(p.createdAt.is)) ~
      ("author" -> p.author.map { _.nickName.is }.openOr("Guest")) ~
      ("content" -> p.content.is) ~
      ("description" -> p.description.is) ~
      ("tags" -> p.tags.map{ _.name.is }) ~
      ("rating" -> p.rating.toString)
  }
}
