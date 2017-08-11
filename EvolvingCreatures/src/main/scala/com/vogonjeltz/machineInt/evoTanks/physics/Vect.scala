package com.vogonjeltz.machineInt.evoTanks.physics

import java.lang.Math._

/**
  * 2D Vector class
  *
  * @param x X-ordinate of the vector
  * @param y Y-ordinate of the vector
  */
case class Vect(x: Double, y: Double) {

  /**
    * Subtract a vector (simply subtracts the ordinates)
    * @param that Vector to subtract
    * @return
    */
  def - (that: Vect) = Vect (x - that.x, y - that.y)

  /**
    * Add a vector
    * @param that Vector to add
    * @return
    */
  def + (that: Vect) = Vect (x + that.x, y + that.y)

  /**
    * Divide by a scalar (divides each ordinate)
    * @param scalar The scalar to divide by
    * @return
    */
  def / (scalar: Double) = Vect(x / scalar, y / scalar)

  /**
    * Multiply the vector by a scalar
    * @param scalar The scalar to multiply by
    * @return
    */
  def * (scalar: Double) = Vect(x * scalar, y * scalar)

  //def unary_- :Vect = this * -1

  def distance(that:Vect): Double = {
    Math.sqrt(Math.pow(this.x - that.x,2) + Math.pow(this.y - that.y, 2))
  }

  def dot(that: Vect): Double =
    (x * that.x) + (y * that.y)

  def wedge(that: Vect): Double =
    (x * that.x) - (y * that.y)


  def length: Double = distance(Vect.ZERO)

  def abs: Vect = Vect(Math.abs(x), Math.abs(y))

  def normalize: Vect = this / length

  def tangent: Vect = Vect(-y, x)

  def unary_- : Vect = Vect(-x, -y)

  def squared : Double = this dot this

  def theta: Rotation = {
    val angle = Rad(atan(Math.abs(y/x)))
    //println(angle.toDeg)

    if (x < 0) {
      if (y < 0) {
        -Deg(180) + angle
      } else {
        Deg(180) - angle
      }
    } else {
      if (y < 0) {
        -angle
      }else {
        angle
      }
    }
  }

}

object Vect {

  val ZERO = Vect(0,0)

  def fromAMF(theta: Rotation, length: Double): Vect =
    Vect(length * cos(theta.toRad), length * sin(theta.toRad))

  //def unapply(arg: Vect): Option[(Double, Double)] = Some((arg.x, arg.y))

}

