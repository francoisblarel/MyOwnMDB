package models

/**
 * User: francois
 * Date: 22/06/13 
 */

case class User(login : String, password : String)

object UserCompanion {

  def userExists(login : String) = true

  def checkPassword(user : User) = true

}
