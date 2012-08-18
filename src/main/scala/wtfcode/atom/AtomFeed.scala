package wtfcode.atom

import java.util.{TimeZone, Date}
import xml.{Node, NodeSeq}
import java.text.SimpleDateFormat

trait AtomFeed[T] {

  def param: String

  val curPage: Long = try {
    param.toLong
  } catch {
    case e: NumberFormatException => 0
  }

  val hasNext = (curPage + 1) * itemsPerPage < count

  def entries: List[T]

  def itemsPerPage = 20

  def count: Long

  def path: String

  def nextLink: NodeSeq = <link rel="next" href={"/atom/" + path + "/" + (curPage + 1) }/>

  def feedId: String

  def feedUpdated: Date

  def feed(): Node = {
    <feed xmlns="http://www.w3.org/2005/Atom"
          xmlns:at="http://purl.org/atompub/tombstones/1.0">
      {if (hasNext) nextLink}
      <title>WtfCode</title>
      <id>{feedId}</id>
      <updated>{format(feedUpdated)}</updated>
      {entries.flatMap(entryToAtom)}
    </feed>
  }

  def entryId(entry: T): String

  def entryDeleted(entry: T): Boolean

  def entryDeletedAt(entry: T): Date

  def entryTitle(entry: T): String

  def entryUpdated(entry: T): Date

  def entryAuthorName(entry: T): String

  def entryContent(entry: T): NodeSeq

  def entryToAtom(entry: T): NodeSeq = {
    if (entryDeleted(entry))
      deletedEntry(entry)
    else
      normalEntry(entry)
  }

  def normalEntry(entry: T): NodeSeq = {
    <entry>
      <id>{entryId(entry)}</id>
      <title>{entryTitle(entry)}</title>
      <updated>{format(entryUpdated(entry))}</updated>
      <author>
        <name>{entryAuthorName(entry)}</name>
      </author>
      <content type="xhtml">
        <div xmlns="http://www.w3.org/1999/xhtml">{entryContent(entry)}</div>
      </content>
    </entry>
  }

  def deletedEntry(entry: T): NodeSeq = {
    <at:deleted-entry
      ref={entryId(entry)}
      when={format(entryDeletedAt(entry))}/>
  }

  def format(date: Date): String = {
    val atomFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    atomFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    atomFormat.format(date)
  }
}
