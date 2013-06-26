package controllers

import play.api.mvc.{Action, Controller}
import models.SessionCompanion

/**
 * User: francois
 * Date: 22/06/13 
 */
object SessionController extends Controller with SecurityTrait{


  def showSession() = isAuthenticated{ username => implicit request =>
    Ok(views.html.session.sessionDetails(SessionCompanion.getActiveSession()))
  }

  def vote(movie : String) = isAuthenticated{ username => implicit request =>
    println(username + " a voté pour le film : " + movie)
    SessionCompanion.addMovieSelection(username, movie)
    Ok
  }

}
