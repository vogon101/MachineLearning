package com.vogonjeltz.machineInt.evoTanks.gfx

import com.vogonjeltz.machineInt.evoTanks.physics.Circle

/**
  * RenderPrimitive
  * TODO: Replace ShapeRenderer with this system?
  *
  * Created by fredd
  */
abstract class RenderPrimitive extends Renderable {

}

case class CircleRenderer(circle: Circle) extends RenderPrimitive {

  override def render() = {
  }

}

case class LineSegmentRenderer()
