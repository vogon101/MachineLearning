package com.vogonjeltz.machineInt.evoTanks.simulation

import com.vogonjeltz.machineInt.evoTanks.core.{Action, GameConstants, SimulationObject}
import com.vogonjeltz.machineInt.evoTanks.gfx.{Colour, LineSegmentRenderer, RenderParams, RenderPrimitive}
import com.vogonjeltz.machineInt.evoTanks.physics.{LineSegment, Vect}

/**
  * VisionRay
  *
  * Created by fredd
  */
class VisionRay (
                val start: Vect,
                val end: Vect,
                val owner: Tank,
                val index: Int = 0
                )
  extends SimulationObject {

  override val layer: Int = GameConstants.GAME_LAYER

  def shape: LineSegment = LineSegment(start, end)

  override def update(): List[Action] = List()

  override def render(): Seq[RenderPrimitive] = List(
    LineSegmentRenderer(shape, RenderParams(colour = Colour.RED))
  )

}
