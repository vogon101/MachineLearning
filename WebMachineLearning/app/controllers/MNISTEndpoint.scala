package controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import resources.{ApiError, MNISTResources}

/**
  * Created by Freddie on 10/06/2017.
  */
class MNISTEndpoint extends Controller {

  def index = Action {

    Ok(views.html.mnist.mnist_home())

  }

  def recognise = Action { implicit request =>

    val body = request.body.asFormUrlEncoded.get("body")
    val in = MNISTResources.parseData(body.head)
    in match {
      case Right(x) => BadRequest(ApiError(x).toJson)
      case Left(image) =>
        val recognise = MNISTResources.recognise(image)
        println(recognise._1)
        println(recognise._2.mkString(","))
        println()
        Ok (Json.prettyPrint(Json.obj("recognised" -> recognise._1, "results" -> recognise._2)))
    }

  }

}
