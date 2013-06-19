import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig}
import play.api.GlobalSettings
import play.api.mvc.{Handler, RequestHeader}

/**
 * User: fblarel
 * Date: 19/06/13 
 */
object Global extends GlobalSettings{

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    proxify()
    super.onRouteRequest(request)
  }

  def proxify() {

//    System.setProperty("proxyHost", "10.21.3.169")
//    System.setProperty("proxyPort", "8888")
  }

}
