package wtfcode.util

import wtfcode.model.User
import net.liftweb.common.{Full, Box}
import net.liftweb.util.SecurityHelpers
import xml.Utility

/**
 * You are free to embed under the terms of the CC-BY license.
 * Example wording might be "Robots lovingly delivered by Robohash.org" or something.
 */
object Avatar {
  val BASE_URL = "http://robohash.org/"

  def apply(user: Box[User], ip: Box[String]) = {
    val url = (user, ip) match {
      case (Full(user), _) => fromUser(user)
      case (_, Full(ip)) => fromIp(ip)
      case _ => fallback
    }
    Utility.escape(url)
  }

  private def fromUser(user: User): String = {
    val hash = md5(user.email.is)
    url(hash) + "&gravatar=hashed"
  }

  private def fromIp(ip: String): String = url(ip.substring(ip.length / 2))

  private def fallback: String = url("42")

  private def url(s: String): String = BASE_URL + s + "?set=set3"

  //SecurityHelpers.md5(in: String) returns base64 encoded hash =(
  private def md5(in: String): String = {
    val bytes = in.getBytes("UTF-8")
    val hashed = SecurityHelpers.md5(bytes)
    SecurityHelpers.hexEncode(hashed)
  }
}
