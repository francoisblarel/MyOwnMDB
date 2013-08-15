package models

import Categorie._
import MovieFormat._
import play.api.libs.functional.syntax._
import org.joda.time.{Duration, DateTime}
import play.api.libs.json.Reads._
import play.api.libs.json._
import java.util.Date

case class Movie(title : String,
                 director : Option[Seq[String]],
                 actors : Option[Seq[String]],
                 year : Option[Int],
                 duration : Option[Duration],
                 categories : Option[Seq[Categorie]],
                 imdbId : Option[Integer],
                 typeMovie : Option[MovieFormat])

case class MovieIMDB(title : String,
                     duration : Option[Seq[String]],
                     poster : Option[String],
                     imdbUrl : String,
                     directors : Option[Seq[String]],
                     writer : Option[Seq[String]],
                     id : String,
                     actors : Option[Seq[String]],
                     plot : Option[String],
                     year : Option[Long],
                     otherTitles : Option[Seq[String]])

case class SearchMovieTMDB(originalTitle : String, title : String, id : Long)

case class MovieTMDB(id : Long,
                     imdbId : String,
                     budget : Option[Int],
                     genres : Seq[Genre],
                     originalTitle : String,
                     pitch : String,
                     posterPath : String,
                     releaseDate : Date,
                     runtime : Int,
                     title : String
                      )
case class Genre(id : Long, libelle : String)


/**
 * User: fblarel
 * Date: 18/06/13 
 */
object MovieCompanion {

  //Movie TMDB
  implicit val movieTMDNReads : Reads[MovieTMDB] =(
    ( __ \ "id").read[Long] and
      ( __ \ "imdb_id").read[String] and
      ( __ \ "budget").readNullable[Int] and
      ( __ \ "genres").lazyRead(Reads.list[Genre]) and
      ( __ \ "original_title").read[String] and
      ( __ \ "overview").read[String] and
      ( __ \ "poster_path").read[String] and
      ( __ \ "release_date").read[Date] and
      ( __ \ "runtime").read[Int] and
      ( __ \ "title").read[String]
    )(MovieTMDB)

  implicit val genreReads : Reads[Genre] = (
    ( __ \ "id").read[Long] and
      ( __ \ "name").read[String]
    )(Genre)


  //SearchMovieTMDB
  implicit val listMovieSearchTMDBRead : Reads[Seq[SearchMovieTMDB]] =(
    ( __ \\ "results").lazyRead(Reads.seq[SearchMovieTMDB]( movieSearchTMDBRead ))
    )

  implicit val movieSearchTMDBRead : Reads[SearchMovieTMDB] =(
    ( __ \ "original_title").read[String] and
      ( __ \ "title").read[String] and
      ( __ \ "id").read[Long]
    )(SearchMovieTMDB)


  //Movie IMDB
  implicit val listMovieIMDBRead : Reads[Seq[MovieIMDB]] = (
    ( __ ).lazyRead(Reads.seq[MovieIMDB]( movieIMDBRead ))
    )

  implicit val movieIMDBRead : Reads[MovieIMDB] = (
    ( __ \ "title").read[String] and
      ( __ \ "runtime").readNullable[Seq[String]] and
      ( __ \ "poster").readNullable[String] and
      ( __ \ "imdb_url").read[String] and
      ( __ \ "directors").readNullable[Seq[String]] and
      ( __ \ "writers").readNullable[Seq[String]] and
      ( __ \ "imdb_id").read[String] and
      ( __ \ "actors").readNullable[Seq[String]] and
      ( __ \ "plot_simple").readNullable[String] and
      ( __ \ "year").readNullable[Long] and
      ( __ \ "also_known_as").readNullable[Seq[String]]
    )(MovieIMDB)


  implicit val movieIMDBWrite : Writes[MovieIMDB] = (
    ( __ \ "title").write[String] and
      ( __ \ "duration").write[Option[Seq[String]]] and
      ( __ \ "poster").write[Option[String]] and
      ( __ \ "imdbUrl").write[String] and
      ( __ \ "directors").write[Option[Seq[String]]] and
      ( __ \ "writer").write[Option[Seq[String]]] and
      ( __ \ "id").write[String] and
      ( __ \ "actors").write[Option[Seq[String]]] and
      ( __ \ "plot").write[Option[String]] and
      ( __ \ "year").write[Option[Long]] and
      ( __ \ "otherTitles").write[Option[Seq[String]]]
    )(unlift(MovieIMDB.unapply _))

  var myMovies = Set(
            Movie("Braindead", Some(Seq("Peter Jackson")), Some(Seq("actors")), Some(1990), Some(Duration.standardMinutes(90)), Some(Seq(Comedy, Horror)), None, Some(AVI)),
            Movie("Il était une fois dans l'ouest", Some(Seq("Sergio Leone")), Some(Seq("Charles Bronson")), Some(1980), Some(Duration.standardMinutes(180)), Some(Seq(Western)), None, Some(DVD)),
            Movie("Pulp Fiction", Some(Seq("Quentin Tarantino")), Some(Seq("Samuel L. Jackson, Uma Thurman, Bruce Willis ")), Some(1994), Some(Duration.standardMinutes(180)), Some(Seq(Action)), None, Some(BLURAY))
                    )


  def find(title : String) = myMovies.find(_.title==title)

  def save(movie : Movie) = if(!myMovies.exists(_.title==movie.title))
                                myMovies = myMovies + movie
                            else
                              println(movie.title + " existe déjà dans la Base de données.")

  def findAll() = myMovies.toList.sortBy(_.title)


}
