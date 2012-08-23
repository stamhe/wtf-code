package wtfcode.util

import java.util.Date
import org.joda.time._
import net.liftweb.http.S

object TimeSpanFormatter {

  implicit def int(value: Int) = java.lang.Integer.valueOf(value)

  def apply(past: Date): String = {
    val interval= new Interval(new DateTime(past), new DateTime)
    val period = interval.toPeriod
    if (period.getYears >= 1) {
      WtfDateTimeConverter.formatDate(past)
    } else if (period.getMonths > 0) {
      S ?? ("period.monthsAgo", int(period.getMonths))
    } else if (period.getDays > 0) {
      S ?? ("period.daysAgo", int(period.getDays))
    } else if (period.getHours > 0) {
      S ?? ("period.hoursAgo", int(period.getHours))
    } else if (period.getMinutes > 0) {
      S ?? ("period.minutesAgo", int(period.getMinutes))
    } else S ? "period.justNow"
  }
}