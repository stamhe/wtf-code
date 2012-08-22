package wtfcode.util

import wtfcode.model.User
import net.liftweb.common.Box
import net.liftweb.util.SecurityHelpers
import xml.Utility

/**
 * You are free to embed under the terms of the CC-BY license.
 * Example wording might be "Robots lovingly delivered by Robohash.org" or something.
 */
object Avatar {
  val BASE_URL = "http://robohash.org/"

  def apply(user: Box[User], ip: String) = {
    val url = user.map(fromUser _).getOrElse(fromIp(ip))
    Utility.escape(url)
  }

  def fromUser(user: User): String = {
    val hash = md5(user.email.is)
    url(hash) + "&gravatar=hashed"
  }

  def fromIp(ip: String): String = if (ip != null) url(ip.substring(ip.length / 2)) else url("None")

  private def url(s: String): String = BASE_URL + s + "?set=set3"

  //SecurityHelpers.md5(in: String) returns base64 encoded hash =(
  def md5(in: String): String = {
    val bytes = in.getBytes("UTF-8")
    val hashed = SecurityHelpers.md5(bytes)
    SecurityHelpers.hexEncode(hashed)
  }
}
