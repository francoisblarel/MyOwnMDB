package controllers

import play.api._
import play.api.mvc._
import scala.io.Source
import play.api.libs.iteratee.{Enumeratee, Enumerator, Iteratee}
import scala.concurrent._
import play.api.libs.json._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import models._
import models.MovieCompanion._
import org.joda.time.Duration
import java.net.{URLEncoder}
import java.io.File
import scala.util.Try
import models.MovieIMDB
import scala.Some
import models.Movie


object Application extends Controller with SecurityTrait {

  /**
   * Accueil du site
   * @return
   */
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
    Async{
        movies.map(x => Ok(Json.toJson(x.get)))
    }

  }


   /** Lit une source ligne par ligne
    * et génére un enumerator
    * @param source
    * @return
    */
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


  /**
   * Upload un csv afin de pouvoir ajouter son contenu en base.
   * @return
   */
  def upload()= Action(parse.multipartFormData){ request =>
    request.body.file("moviesFile").map(f => createMoviesFromFile(f.ref.file))
    Redirect(routes.Application.list())
  }


  /**
   * Ajoute en base de donnée un ensemble de films qui se trouvent dans un fichier csv.
   * @param f : le fichier contenant la liste des films à ajouter
   */
  def createMoviesFromFile(f : File){
    val file = Source.fromFile(f)(scala.io.Codec.ISO8859)

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
