package wtfcode.atom

import net.liftweb.http.{AtomResponse, GetRequest, Req, LiftRules}
import net.liftweb.common.Full

object AtomDispatcher {

  def dispatch: LiftRules.DispatchPF = {
    case Req(List("atom", "posts"), _, GetRequest) =>
      () => Full(AtomResponse(PostFeed.feed()))

    case Req(List("atom", "comments"), _, GetRequest) =>
      () => Full(AtomResponse(CommentFeed.feed()))
  }

}
