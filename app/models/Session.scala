package models

import org.joda.time.{Instant, DateTime}

/**
 * User: francois
 * Date: 22/06/13 
 */
case class Vote(user : String, movieTitle : String)
case class VoteSession(date : DateTime, selection : Seq[Vote])

object SessionCompanion{

  var testSession = VoteSession(Instant.now().toDateTime, Seq(Vote("francois", "Brazil"), Vote("francois","Heat"), Vote("Camille", "Dragons")))

  def getActiveSession() = testSession

  def addMovieSelection(user : String, movieTitle : String) = {
    getActiveSession().selection :+ Vote(user,movieTitle)
  }


}
