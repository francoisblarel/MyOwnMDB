package controllers

import play.api.mvc._
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Security._

/**
 * User: francois
 * Date: 22/06/13 
 */
trait SecurityTrait {

  /**
   * Récupère l'utilisateur en session
   * @param request
   * @return
   */
  def username(request: RequestHeader) = request.session.get("userConnected")

  /**
   * Si on n'est pas loggué, on redirige vers la page de login
   * @param request
   * @return
   */
  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.LoginController.login)

  /**
   * Action qui vérifie que l'utilisateur est bien connecté.
   * @param f
   * @return
   */
  def isAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

}
