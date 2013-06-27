package controllers

import play.api.mvc.{Action, Controller}
import models.SessionCompanion
import play.api.libs.iteratee.{Enumeratee, Enumerator}
import play.api.libs.concurrent.Promise
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.EventSource
import play.api.libs.json.{JsValue, Json}

/**
 * User: francois
 * Date: 22/06/13 
 */
object SessionController extends Controller with SecurityTrait{

  //val logVotes = Enumerator[String]

  def showSession() = isAuthenticated{ username => implicit request =>
    Ok(views.html.session.sessionDetails(SessionCompanion.getActiveSession()))
  }

  def vote(movie : String) = isAuthenticated{ username => implicit request =>
    println(username + " a voté pour le film : " + movie)
    SessionCompanion.addMovieSelection(username, movie)
    Ok
  }


  def unvote(movie : String) = isAuthenticated{ username => implicit request =>
    println(username + " a retiré son vote pour le film : " + movie)
    SessionCompanion.removeMovieSelection(username, movie)
    Ok
  }


  def feed() = Action{

    val b : Enumerator[List[String]]= Enumerator.generateM(
      Promise.timeout(SessionCompanion.getNewVotes().orElse(Option(List())) ,2000)
    )

    val asJson : Enumeratee[List[String], JsValue] = Enumeratee.map( l => Json.toJson(l))

    Ok.feed(b &> asJson &> EventSource()).as("text/event-stream")
  }

}
