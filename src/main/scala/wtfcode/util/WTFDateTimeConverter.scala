package wtfcode.util

import net.liftweb.util.{DefaultDateTimeConverter, DateTimeConverter}
import java.util.Date
import net.liftweb.http.S
import java.text.DateFormat
import wtfcode.model.User

object WtfDateTimeConverter extends DateTimeConverter {
  def formatDateTime(date: Date) = {
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, S.locale)
    format(date, dateFormat)
  }

  def formatDate(date: Date) = {
    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, S.locale)
    format(date, dateFormat)
  }

  def formatTime(date: Date) = {
    val dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, S.locale)
    format(date, dateFormat)
  }

  private def format(date: Date, format: DateFormat): String = {
    User.currentUser.map(user => format.setTimeZone(user.timezone.isAsTimeZone))
    format.format(date)
  }

  def parseDateTime(s: String) = DefaultDateTimeConverter.parseDateTime(s)

  def parseDate(s: String) = DefaultDateTimeConverter.parseDate(s)

  def parseTime(s: String) = DefaultDateTimeConverter.parseTime(s)
}
