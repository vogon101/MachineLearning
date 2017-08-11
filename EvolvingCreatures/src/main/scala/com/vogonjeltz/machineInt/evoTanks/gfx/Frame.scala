package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.physics.{Rotation, Vect}

/**
  * Frame
  *
  * Created by fredd
  */
final case class Frame(private val _position: Vect = null, private val _colour: Colour = null, private val _rotation: Rotation = null) {

  val position = Option(_position)

  val rotation = Option(_rotation)

  val colour = Option(_colour)

  def wrap (actions: => Unit) = Render.withContext(this)(actions)

  def apply(c: Colour)  = Frame(_position, c, _rotation)
  def apply(p: Vect)    = Frame(p, _colour, _rotation)
  def apply(r: Rotation)= Frame(_position, _colour, r)

}
