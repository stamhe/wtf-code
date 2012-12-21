package wtfcode.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import wtfcode.model.Comment

/**
 * Server object that handles incoming messages
 * from posting users.
 *
 * @author Roman Kashitsyn
 */
object CommentServer extends LiftActor with ListenerManager {

  var comments : List[Comment] = Nil

  def createUpdate = comments

  override def lowPriority = {
    case comment: Comment => {
      comments = comment :: Nil
      updateListeners()
    }
  }
}

