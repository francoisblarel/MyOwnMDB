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
  
  /**
   * A partir de la liste des votes de la session, retourne une map avec pour clé le
   * film (groupBy) et pour valeur la liste des votants.
   */
   def getSelectionByMovie : Map[String, Seq[(String)]] = {
     // à ameliorer +lisibilité
    testSession.selection.groupBy(_.movieTitle).mapValues(_.map(v => v.user))        
  }


}
