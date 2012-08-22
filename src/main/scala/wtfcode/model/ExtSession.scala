package wtfcode.model

import net.liftweb.mapper.{ProtoExtendedSession, MetaProtoExtendedSession}
import net.liftweb.common.Box

class ExtSession extends ProtoExtendedSession[ExtSession] {
  def getSingleton = ExtSession
}

object ExtSession extends ExtSession with MetaProtoExtendedSession[ExtSession] {
  override def dbTableName = "ext_sessions"

  def logUserIdIn(uid: String): Unit = User.logUserIdIn(uid)

  def recoverUserId: Box[String] = User.currentUserId

  type UserType = User
}
