package controllers

import play.api._
import play.api.mvc._
import scala.io.Source
import play.api.libs.iteratee.{Enumeratee, Enumerator, Iteratee}
import scala.concurrent._
import play.api.libs.json._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS
import models.{MovieIMDB, MovieCompanion, Categorie, Movie}
import org.joda.time.Duration
import java.net.{URLEncoder}
import java.io.File
import scala.util.Try


object Application extends Controller with SecurityTrait {


  def index = isAuthenticated {username =>  implicit request =>
    Redirect(routes.Application.list())
  }


  /**
   * Retourne la liste des films qui sont en BDD
   */
  def list = isAuthenticated {username => implicit request =>
    val movies = MovieCompanion.findAll()
    println(movies)
    Ok(views.html.movies.list(movies, "Your new application is ready."))
  }


  /**
   * Récupère en BDD les données d'un film
   * @param title le titre du film a retrouver.
   */
  def show(title : String) = isAuthenticated {username => implicit request =>
    MovieCompanion.find(title).map(mov =>
      Ok(views.html.movies.details(mov))
    ).getOrElse(NotFound)
  }


  /**
   * A partir d'un titre de film, récupère les infos dans IMDB
   * @param title  le titre du film
   */
  def showFromIMDB(title : String) = isAuthenticated {username => implicit request =>
    /*
   Voir API IMDB : http://mymovieapi.com/
   http://mymovieapi.com/?title=Drive&type=json&plot=simple&episode=0&limit=2&yg=0&mt=M&lang=en-US&offset=&aka=simple&release=simple&business=0&tech=0
   */
      println("recherche de : " + title)

      val url : String = """http://mymovieapi.com/?title=""" + URLEncoder.encode(title, "UTF-8") +"""&type=json&plot=simple&episode=0&limit=2&yg=0&mt=M&lang=en-US&offset=&aka=simple&release=simple&business=0&tech=0"""
      println("URL : " + url)
      val movies : Future[Option[MovieIMDB]]= WS.url(url)
                  .get()
                  .map(r => {
                    println(r.status + " : " + r.json)
                    // TODO : a refactorer : ligne qui fait mal au crâne!
                    r.json.as[Seq[MovieIMDB]].find(m =>
                      m.title.toLowerCase == title.toLowerCase
                        || ( if(m.otherTitles.isDefined)
                                {m.otherTitles.get.exists(_.toLowerCase()==title.toLowerCase())} else false
                            )
                      )
                    }
                  ).recover{
                    case e : Exception =>{
                      println("BOOM : " + e.getMessage() + ", cause : "+e.getCause + " \nexception " + e)
                      None
                    }
                  }

    // Humanis proxy compliant : on simule une requête qui dure 3sec
//    val fut : Future[String] = Promise.timeout(
//
//    """[{"runtime":["104 min","Argentina: 99 min","Germany: 94 min","USA: 85 min (R-rated version)","USA: 97 min (unrated version)","Germany: 80 min (FSK 16 version)","Finland: 92 min (cut version) (1992) (1993)","South Korea: 85 min (heavily cut)","Spain: 99 min (DVD edition)"],"rating":7.6,"genres":["Comedy","Horror"],"rated":"UNRATED","language":["English","Spanish"],"title":"Braindead","filming_locations":"Karori Cemetery, Karori, Wellington, New Zealand","poster":"http://ia.media-imdb.com/images/M/MV5BMTcwMzY5MTYxNF5BMl5BanBnXkFtZTYwOTUwOTc4._V1._SY317_CR5,0,214,317_.jpg","imdb_url":"http://www.imdb.com/title/tt0103873/","writers":["Stephen Sinclair","Stephen Sinclair"],"imdb_id":"tt0103873","directors":["Peter Jackson"],"rating_count":52263,"actors":["Timothy Balme","Diana Peñalver","Elizabeth Moody","Ian Watkin","Brenda Kendall","Stuart Devenie","Jed Brophy","Stephen Papps","Murray Keane","Glenis Levestam","Lewis Rowe","Elizabeth Mulfaxe","Harry Sinclair","Davina Whitehouse","Silvio Famularo"],"plot_simple":"A young man's mother is bitten by a Sumatran rat-monkey. She gets sick and dies, at which time she comes back to life, killing and eating dogs, nurses, friends, and neighbors.","year":1992,"country":["New Zealand"],"type":"M","release_date":19930212,"also_known_as":["Dead Alive"]},{"rating":6.7,"genres":["Short"],"language":["English"],"title":"Braindead","country":["USA"],"imdb_url":"http://www.imdb.com/title/tt0319103/","imdb_id":"tt0319103","directors":["Jon Moritsugu"],"rating_count":1270,"year":1987,"runtime":["1 min"],"type":"M"}]"""

//      """[{"runtime": ["113 min", "Spain: 110 min (cut version)"], "rating": 8.5, "genres": ["Crime", "Drama"], "rated": "R", "language": ["English", "Spanish"], "title": "Taxi Driver", "filming_locations": "13 St between 2nd &amp; 3rd Avenues, Manhattan, New York City, New York, USA", "poster": "http://ia.media-imdb.com/images/M/MV5BMTQ1Nzg3MDQwN15BMl5BanBnXkFtZTcwNDE2NDU2MQ@@._V1._SY317_CR9,0,214,317_.jpg", "imdb_url": "http://www.imdb.com/title/tt0075314/", "writers": ["Paul Schrader"], "imdb_id": "tt0075314", "directors": ["Martin Scorsese"], "rating_count": 265826, "actors": ["Diahnne Abbott", "Frank Adu", "Victor Argo", "Gino Ardito", "Garth Avery", "Peter Boyle", "Albert Brooks", "Harry Cohn", "Copper Cunningham", "Robert De Niro", "Brenda Dickson", "Harry Fischler", "Jodie Foster", "Nat Grant", "Leonard Harris"], "plot_simple": "A mentally unstable Vietnam war veteran works as a nighttime taxi driver in New York City where the perceived decadence and sleaze feeds his urge to violently lash out, attempting to save a teenage prostitute in the process.", "year": 1976, "country": ["USA"], "type": "M", "release_date": 19760208, "also_known_as": ["\u039f \u03a4\u03b1\u03be\u03b9\u03c4\u03b6\u03ae\u03c2"]}, {"runtime": ["100 min"], "rating": 7.9, "genres": ["Crime", "Drama"], "rated": "R", "language": ["English"], "title": "Drive", "filming_locations": "Saugus Speedway - 22500 Soledad Canyon Road, Saugus, California, USA", "poster": "http://ia.media-imdb.com/images/M/MV5BOTM1ODQ0Nzc4NF5BMl5BanBnXkFtZTcwMTM0MjQyNg@@._V1._SY317_.jpg", "imdb_url": "http://www.imdb.com/title/tt0780504/", "writers": ["Hossein Amini", "James Sallis"], "imdb_id": "tt0780504", "directors": ["Nicolas Winding Refn"], "rating_count": 237433, "actors": ["Ryan Gosling", "Carey Mulligan", "Bryan Cranston", "Albert Brooks", "Oscar Isaac", "Christina Hendricks", "Ron Perlman", "Kaden Leos", "Jeff Wolfe", "James Biberi", "Russ Tamblyn", "Joe Bucaro III", "Tiara Parker", "Tim Trella", "Jim Hart"], "plot_simple": "A mysterious Hollywood stuntman, mechanic and getaway driver lands himself in trouble when he helps out his neighbour.", "year": 2011, "country": ["USA"], "type": "M", "release_date": 20110916, "also_known_as": ["\u0158idi\u010d"]}]"""
//      ,2000)
//
//    Async{
//      fut.map(r => {
//        val j = Json.parse(r)
////        j.as[Seq[MovieIMDB]]
//        j.as[Seq[MovieIMDB]]
//        println("ma liiste"+j)
//        Ok(r)
//      })
//    }

    Async{
        movies.map(x => Ok(views.html.movies.imdbDetails(x.get)))
    }

  }


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




  //  Lit une source ligne par ligne
  def lineEnumerator(source: Source) : Enumerator[String] = {
    val lines = source.getLines()

    Enumerator.fromCallback1[String] ( _ => {
      val line = if (lines.hasNext) {
        Some(lines.next())
      } else {
        None
      }
      Future.successful(line)
    })
  }

  /**
   *
   * Parse les lignes du fichier, et récupère les données des films si possible pour les sauvegarder en BDD
   */
  def charge() = isAuthenticated {username => implicit request =>

    val file = Source.fromFile(Play.getExistingFile("public/files/movies.txt").get)

    val lineParser : Enumeratee[String, Option[Movie]] = Enumeratee.map(line =>
      line.split(";") match{

        case Array(title, director, actors, year, duration, _)
            => Some(Movie(title, Some(director.split(",").toSeq), Some(actors.split(",").toSeq),
                    Some(year.toInt), Some(Duration.standardMinutes(duration.toLong)), Some(Seq(Categorie.Action)), None, None))
        case _ => println("chargement échoué");None
      }
    )

    println("Chargement des nouveaux films...")
    lineEnumerator(file) &> lineParser run(Iteratee.foreach(mov => MovieCompanion.save(mov.get)))
    Redirect(routes.Application.list())
  }


  def upload()= Action(parse.multipartFormData){ request =>
    request.body.file("moviesFile").map(f => createMoviesFromFile(f.ref.file))
    Redirect(routes.Application.list())
  }

  def createMoviesFromFile(f : File){
    val file = Source.fromFile(f)

    val lineParser : Enumeratee[String, Option[Movie]] = Enumeratee.map(line => {
      println("ligne : " + line)
      line.replaceAll("\"","").split(";") match{

          case a @ Array(title, _*) => Some( Movie(title,
                                                     Try(a(1).split(",").toSeq).toOption, //directors
                                                     Try(a(2).split(",").toSeq).toOption, //actors
                                                     Try(a(3).toInt).toOption, //year
                                                     Try(Duration.standardMinutes(a(4).toLong)).toOption, //duration
                                                     Option(Seq(Categorie.Action)), //category
                                                     None, //
                                                     None)
                                                    )
       // case Array(title, director, actors, year, duration, _)
        //=> Some(Movie(title, Some(director.split(",").toSeq), Some(actors.split(",").toSeq),
//          Some(year.toInt), Some(Duration.standardMinutes(duration.toLong)), Some(Seq(Categorie.Action)), None))
        case _ => println("chargement échoué");None
      }}
    )

    println("Chargement des nouveaux films...")
    lineEnumerator(file) &> lineParser run(Iteratee.foreach(mov => MovieCompanion.save(mov.get)))
  }
}
