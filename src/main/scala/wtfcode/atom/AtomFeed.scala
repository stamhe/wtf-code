package wtfcode.atom

import java.util.Date
import xml.{Node, NodeSeq}

trait AtomFeed[T] {

  def entries: List[T]

  def feedId: String

  def feedUpdated: Date

  def feed(): Node = {
    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>WtfCode</title>
      <id>{feedId}</id>
      <updated>{format(feedUpdated)}</updated>
      {entries.flatMap(entryToAtom)}
    </feed>
  }

  def entryId(entry: T): String

  def entryTitle(entry: T): String

  def entryUpdated(entry: T): Date

  def entryAuthorName(entry: T): String

  def entryContent(entry: T): NodeSeq

  def entryToAtom(entry: T): NodeSeq = {
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

  def format(date: Date): String = AtomDate.format(date)
}
