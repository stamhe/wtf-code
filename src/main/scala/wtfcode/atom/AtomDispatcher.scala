package wtfcode.atom

import net.liftweb.http.{AtomResponse, GetRequest, Req, LiftRules}
import net.liftweb.common.Full

object AtomDispatcher {

  def dispatch: LiftRules.DispatchPF = {
    case Req(List("atom", "posts"), _, GetRequest) =>
      () => Full(AtomResponse(new PostFeed("0").feed()))
    case Req(List("atom", "posts", param), _, GetRequest) =>
      () => Full(AtomResponse(new PostFeed(param).feed()))

    case Req(List("atom", "comments"), _, GetRequest) =>
      () => Full(AtomResponse(new CommentFeed("0").feed()))
    case Req(List("atom", "comments", param), _, GetRequest) =>
      () => Full(AtomResponse(new CommentFeed(param).feed()))
  }

}
