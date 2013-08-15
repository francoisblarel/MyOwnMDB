package models

import play.api.libs.json._
import models.ImagesConfig
import models.Configuration
import play.api.libs.functional.syntax._

case class Configuration(imageConfig : ImagesConfig, changeKeys : Seq[String])
case class ImagesConfig(baseUrl : String, secureBaseUrl : String, posterSizes : Seq[String], backdropSizes : Seq[String], profileSizes : Seq[String], logoSizes : Seq[String])


/**
 * User: francois
 * Date: 04/08/13 
 */
object ConfigurationCompanion {



  implicit val  configurationReads : Reads[Configuration] = (
    ( __ \ "images").lazyRead(Reads.of[ImagesConfig]) and
      ( __ \ "change_keys").read[Seq[String]]
    )(Configuration)

  implicit val imagesConfigReads : Reads[ImagesConfig] = (
    ( __ \ "base_url").read[String] and
      ( __ \ "secure_base_url").read[String] and
      ( __ \ "poster_sizes").read[Seq[String]] and
      ( __ \ "backdrop_sizes").read[Seq[String]] and
      ( __ \ "profile_sizes").read[Seq[String]] and
      ( __ \ "logo_sizes").read[Seq[String]]
    )(ImagesConfig)

}
