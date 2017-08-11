package com.vogonjeltz.machineInt.evoTanks.simulation

import com.vogonjeltz.machineInt.evoTanks.core._
import com.vogonjeltz.machineInt.evoTanks.gfx.ShapeRenderer
import com.vogonjeltz.machineInt.evoTanks.physics.{Circle, Vect}

/**
  * Bullet
  *
  * Created by fredd
  */
class Bullet(private var _position: Vect, val velocity: Vect, val shooter: Tank) extends SimulationObject {

  override val name = "Bullet"

  override val layer: Int = GameConstants.GAME_LAYER

  override def shape = Circle(GameConstants.BULLET_SIZE, _position)

  private var age = 0

  override def render(): Unit = {
    ShapeRenderer.render(shape)
  }

  override def update(): List[Action] = ActionAggregator().aggregate { implicit a =>
    age += 1
    if (age > GameConstants.BULLET_LIFETIME) RemoveObjectAction(this)
    _position += velocity
  }

}

object Bullet {

  def killAction(b: Bullet, t: Tank): List[Action] = if (b.shooter == t) List() else List(
    RemoveObjectAction(t),
    RemoveObjectAction(b)
  )

}