package resources

import play.api.libs.json.{JsValue, Json}

/**
  * Created by Freddie on 10/06/2017.
  */
case class ApiError(error:String) {

  def toJson: JsValue = Json.obj(("error", Json.toJsFieldJsValueWrapper(error)))

}
