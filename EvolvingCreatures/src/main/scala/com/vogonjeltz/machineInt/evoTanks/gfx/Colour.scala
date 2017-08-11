package com.vogonjeltz.machineInt.evoTanks.gfx

/**
  * Colour
  *
  * Created by fredd
  */
final case class Colour(r: Double, g: Double, b: Double) {

  def tuple: (Double, Double, Double) = (r,g,b)
  //TODO: Alpha

}

object Colour {
  val BLACK = Colour( 0.0, 0.0, 0.0 )
  val BLUE = Colour( 0.0, 0.0, 1.0 )
  val CYAN = Colour( 0.0, 1.0, 1.0 )
  val DARK_BLUE = Colour( 0.1, 0.1, 0.3 )
  val DARK_CYAN = Colour( 0.0, 0.3, 0.3 )
  val DARK_GREEN = Colour( 0.1, 0.2, 0.1 )
  val DARK_GREY = Colour( 0.2, 0.2, 0.2 )
  val DARK_MAGENTA = Colour( 0.3, 0.0, 0.3 )
  val DARK_RED = Colour( 0.3, 0.1, 0.1 )
  val DARK_YELLOW = Colour( 0.3, 0.3, 0.0 )
  val GREEN = Colour( 0.0, 1.0, 0.0 )
  val LIGHT_BLUE = Colour( 0.8, 0.8, 1.0 )
  val LIGHT_CYAN = Colour( 8.0, 1.0, 1.0 )
  val LIGHT_GREEN = Colour( 0.8, 1.0, 0.8 )
  val LIGHT_GREY = Colour( 0.8, 0.8, 0.8 )
  val LIGHT_MAGENTA = Colour( 1.0, 0.8, 1.0 )
  val LIGHT_RED = Colour( 1.0, 0.8, 0.8 )
  val LIGHT_YELLOW = Colour( 1.0, 1.0, 0.8 )
  val MAGENTA = Colour( 1.0, 0.0, 1.0 )
  val RED = Colour( 1.0, 0.0, 0.0 )
  val WHITE = Colour( 1.0, 1.0, 1.0 )
  val YELLOW = Colour( 1.0, 1.0, 0.0 )
}
