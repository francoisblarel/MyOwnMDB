import models.{ImagesConfig, Configuration}
import play.api.{Play, GlobalSettings, Application}
import play.api.libs.ws.WS
import play.api.mvc.{Handler, RequestHeader}
import models.ConfigurationCompanion._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current


/**
 * User: fblarel
 * Date: 19/06/13 
 */
object Global extends GlobalSettings{

  val TMDB_CONFIG_URL = """http://api.themoviedb.org/3/configuration?api_key=324b48a53b30f9e126d1580e9c706a6d"""

//  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
//    proxify()
//    super.onRouteRequest(request)
//  }

  def proxify() {
//    System.setProperty("proxyHost", "10.21.3.169")
//    System.setProperty("proxyPort", "8888")
  }

  override def onStart(app : Application){
   chargeTMDBConfiguration
    super.onStart(app)
  }


  def chargeTMDBConfiguration(){
    WS.url(TMDB_CONFIG_URL).get().map(r => {
      println(r.status + " : " +r.json)
      val res =  r.json.as[Configuration]
      println("RES "+res)
    }
    ).recover({
      case e : Exception =>
        println("Erreur lors de la récupération de la configuration TMDB "+e)
      }
    )
  }



}
