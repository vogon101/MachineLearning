package controllers

import play.api.mvc.{Action, Controller}

/**
  * EconEndpoint
  *
  * Created by fredd
  */
class EconEndpoint extends Controller {

  def kalman = Action {
    Ok(views.html.econ.kalman.writeup())
  }

}
