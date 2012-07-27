package wtfcode.util

/**
 * You are free to embed under the terms of the CC-BY license.
 * Example wording might be "Robots lovingly delivered by Robohash.org" or something.
 */
object RoboHash {
  val BASE_URL = "http://robohash.org/"
  val SIZE = 100

  def url(s: String): String = BASE_URL + s + "?set=set3&size=" + SIZE + "x" + SIZE

  def fromIp(ip: String): String = if (ip != null) url(ip.substring(ip.length / 2)) else url("None")
}
