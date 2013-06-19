package models

import Categorie._
import play.api.libs.functional.syntax._
import org.joda.time.{Duration, DateTime}
import play.api.libs.json.Reads._
import play.api.libs.json._

case class Movie(title : String,
                 director : Option[Seq[String]],
                 actors : Option[Seq[String]],
                 year : Option[Integer],
                 duration : Option[Duration],
                 categories : Option[Seq[Categorie]],
                 imdbId : Option[Integer])

case class MovieIMDB(duration : Seq[String],
                     poster : Option[String],
                     imdbUrl : String,
                     directors : Seq[String],
                     writer : Option[Seq[String]],
                     id : String,
                     actors : Option[Seq[String]],
                     plot : Option[String],
                     year : Long)

/**
 * User: fblarel
 * Date: 18/06/13 
 */
object MovieCompanion {

  var myMovies = Set(
            Movie("Braindead", Some(Seq("Peter Jackson")), Some(Seq("actors")), Some(1990), Some(Duration.standardMinutes(90)), Some(Seq(Comedy, Horror)), None),
            Movie("Il était une fois dans l'ouest", Some(Seq("Sergio Leone")), Some(Seq("Charles Bronson")), Some(1980), Some(Duration.standardMinutes(180)), Some(Seq(Western)), None),
            Movie("Pulp Fiction", Some(Seq("Quentin Tarantino")), Some(Seq("Samuel L. Jackson, Uma Thurman, Bruce Willis ")), Some(1994), Some(Duration.standardMinutes(180)), Some(Seq(Action)), None)
                    )


  def find(title : String) = myMovies.find(_.title==title)

  def save(movie : Movie) = if(!myMovies.exists(_.title==movie.title))
                                myMovies = myMovies + movie
                            else
                              println(movie.title + " existe déjà dans la Base de données.")

  def findAll() = myMovies.toList.sortBy(_.title)


}