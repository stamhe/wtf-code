package wtfcode.util

import net.liftweb.util.{DefaultDateTimeConverter, DateTimeConverter}
import java.util.Date
import net.liftweb.http.S
import java.text.DateFormat

object WTFDateTimeConverter extends DateTimeConverter {
  def formatDateTime(d: Date) =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, S.locale).format(d)

  def formatDate(d: Date) =
    DateFormat.getDateInstance(DateFormat.MEDIUM, S.locale).format(d)

  def formatTime(d: Date) =
    DateFormat.getTimeInstance(DateFormat.MEDIUM, S.locale).format(d)

  def parseDateTime(s: String) = DefaultDateTimeConverter.parseDateTime(s)

  def parseDate(s: String) = DefaultDateTimeConverter.parseDate(s)

  def parseTime(s: String) = DefaultDateTimeConverter.parseTime(s)
}
