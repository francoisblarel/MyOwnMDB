package controllers

import play.api.mvc.{Flash, Action, Controller}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraints, Constraint}
import play.api.i18n.Messages
import models.{UserCompanion, User}

/**
 * User: francois
 * Date: 22/06/13 
 */
object LoginController extends Controller {



  private val loginForm : Form[User] = Form(
    mapping(
      "login" -> nonEmptyText.verifying("login.validation.user.doesnt.exist", UserCompanion.userExists(_)),
      "password" -> nonEmptyText
    )(User.apply)(User.unapply).verifying("login.validation.user.password.invalid", UserCompanion.checkPassword(_))
  )

  def login = Action{ implicit request =>
  // val theForm = loginForm.bind(flash.data)
    Ok(views.html.authenticate(loginForm))
  }


  def doLogin() = Action{ implicit request =>
    this.loginForm.bindFromRequest().fold(
      hasErrors = {
        form =>
          val newlogin = form.error("login").isDefined
          println(newlogin)
          val errors : String = form.globalError.map(err => Messages(err.message)).getOrElse("")
          Redirect(routes.LoginController.login())
            .flashing(
            "error" -> ("Login error :" + errors),
            "newLogin" -> newlogin.toString
          )
      },
      success = {
        userConnected =>
          Redirect(routes.Application.index())
            .flashing("success" -> ("Bienvenue " +userConnected.login))
            .withSession(request.session + ("userConnected" -> userConnected.login))
      }
    )
  }


  def logout = Action{
    Redirect(routes.LoginController.login()).withNewSession
  }



}
