package com.vogonjeltz.machineInt.evoTanks.physics

/**
  * An angle
  */
sealed trait Rotation {

  protected val DEG2RAD: Double = 180 / Math.PI
  protected val RAD2DEG: Double = Math.PI / 180

  /**
    * @return The angle in degrees
    */
  def toDeg: Double

  /**
    * @return The angle in degrees
    */
  def toRad: Double

  def +(that: Rotation) : Rotation

  def -(that: Rotation) : Rotation

  //TODO: Implement clamping a vector between -180 and 180
  def /(scalar: Double): Rotation = ???

  def unary_- : Rotation

  def > (that: Rotation): Boolean = toDeg > that.toDeg

  def < (that: Rotation): Boolean = toDeg < that.toDeg

  def abs: Rotation

  def to360: Rotation = this + Deg(180)

  override def toString: String = s"Deg($toDeg)"

}

object Rotation {

  /**
    * @return A rotation of zero degrees
    */
  def zero = Deg(0)

  /*
  def capped(r: Rad):Rotation = {
    var _r = r
    while (_r.toDeg > 180) _r = _r-
  }*/

}

/**
  * @param r The angle in radians
  */
case class Rad(r: Double) extends Rotation {

  def toDeg = r * DEG2RAD

  def toRad = r

  def +(that: Rotation) = Rad(r + that.toRad)

  def -(that: Rotation) = Rad(r - that.toRad)

  override def unary_- = Rad(-r)

  override def abs = Rad(Math.abs(r))

}


object Rad {

  /**
    * Converts an angle in degrees to radians
    * @param d The angle in degrees
    * @return The angle in radians
    */
  def apply(d : Deg):Rad = Rad(d.toRad)

}

/**
  * @param d The angle in degress
  */
case class Deg(d: Double) extends Rotation {

  def toDeg = d

  def toRad = d * RAD2DEG

  def +(that:Rotation) = Deg(d + that.toDeg)

  def -(that:Rotation) = Deg(d - that.toDeg)

  override def unary_- = Deg(-d)

  def abs = Deg(Math.abs(d))

}

object Deg {

  /**
    * Converts an angle in radians to degrees
    *
    * @param r The angle in radians
    * @return The angle in degrees
    */
  def apply(r: Rad): Deg = Deg(r.toDeg)

}
