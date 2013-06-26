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
  var logVotes : List[String] = List("hello")

  def getActiveSession() = testSession


  def addMovieSelection(user : String, movieTitle : String) = {
    logVotes = logVotes :+ new String(user + " a voté pour le film " + movieTitle)
    testSession = VoteSession(testSession.date, testSession.selection :+ Vote(user,movieTitle))
  }

  
  type ResulstatVote = (String, Seq[String])
  /**
   * A partir de la liste des votes de la session, retourne une map avec pour clé le
   * film (groupBy) et pour valeur la liste des votants.
   */
  def getSelectionByMovie : List[ResulstatVote] = {
    val grp : Map[String, Seq[String]] = testSession.selection.groupBy(_.movieTitle).mapValues(_.map(v => v.user))
    val l = grp.toList.sortWith( (a,b) => a._2.length > b._2.length)
    println("grp = " + l )
    l
  }

  def getNewVotes() : Option[List[String]] = {
    val tete = if(logVotes.isEmpty) None else Option(logVotes)
    logVotes = List()
    tete
  }

}
