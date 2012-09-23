package wtfcode.util

object LeadingWhitespaceStripper {
  val ws = Array(' ', '\t')

  def apply(code: String): String = {
    val stripN = code.lines.map(countLeadingWS _).min

    code.linesWithSeparators.map(line =>
      if (isEmpty(line))
        "\n"
      else
        line.slice(stripN, Int.MaxValue)
    ).mkString
  }

  private def countLeadingWS(s: String): Int = {
    if (isEmpty(s))
      Int.MaxValue
    else
      s.takeWhile(ws.contains(_)).length
  }

  private def isEmpty(s: String) = {
    s.trim.isEmpty
  }
}
