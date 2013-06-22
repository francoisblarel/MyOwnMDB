package controllers

import play.api.mvc.{Action, Controller}
import models.SessionCompanion

/**
 * User: francois
 * Date: 22/06/13 
 */
object SessionController extends Controller{


  def showSession() = Action{
    Ok(views.html.session.sessionDetails(SessionCompanion.getActiveSession()))
  }

}
