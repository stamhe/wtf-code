package wtfcode.model

/**
 * Represents model with rating.
 * @author <a href="mailto:roman.kashitsyn@gmail.com">Roman Kashitsyn</a>
 */
trait Rated {
  def currentRating: Int

  def canVote(user: User): Boolean

  def voteOn(user: User): Int = updateVotes(user, _ + 1)

  def voteAgainst(user: User): Int  = updateVotes(user, _ - 1)

  protected def updateVotes(user: User, func: Int => Int): Int
}
