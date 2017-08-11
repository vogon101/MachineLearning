package com.vogonjeltz.machineInt.evoTanks.simulation

import com.vogonjeltz.machineInt.evoTanks.core._
import com.vogonjeltz.machineInt.evoTanks.gfx._
import com.vogonjeltz.machineInt.evoTanks.physics.{Circle, Deg, Vect}
import org.lwjgl.opengl.GL11._

import scala.util.Random

/**
  * Created by Freddie on 06/08/2017.
  */
class Tank(protected var _position: Vect) extends SimulationObject {

  /*protected */var _velocity: Vect = Vect.ZERO
  protected var _food = GameConstants.STARTING_FOOD
  def food = _food
  def eat(f: FoodDropping) = _food += f.size

  override val name = "Tank"

  val layer = GameConstants.GAME_LAYER

  def position = _position
  def velocity = _velocity
  def rotation = _velocity.theta

  override def shape = Circle(GameConstants.TANK_SIZE, _position)

  override def render(): Unit = TankRenderer.renderTank(this)

  var i = 0

  override def update(): List[Action] = ActionAggregator().aggregate { implicit a =>
    _food -=0.01
    if (food < 0) RemoveObjectAction(this)

    if (Random.nextDouble() < 0.2)
      ResolvableAction { arena =>

        if (i % 30 == 0)
          arena.objects.filter(O => O.layer == GameConstants.FOOD_LAYER && O.shape.intersects(shape)).flatMap {
            case f: FoodDropping =>
              _food += f.size
              List(RemoveObjectAction(f)(a))
            case _ => List()
          }.toList match {
            case Nil =>
              _food -= 0.2
              List()
            case x: List[Action] => x
          }
        else List()

      }

    _position = _position + velocity
    _velocity += Vect(Random.nextDouble() / 50 - 0.01, Random.nextDouble() / 50 - 0.01)
    i+=1
    if (i % 100 == 0)
      shoot()

  }

  def shoot()(implicit aa: Option[ActionAggregator]): Unit = {
    if (food > 3) {
      AddObjectAction(
        new Bullet(position, velocity * GameConstants.BULLET_SPEED_MULT, this)
      )
      _food -= GameConstants.BULLET_COST
    }
  }


}

object TankRenderer {

  def renderTank(t: Tank): Unit = {
    val colour = Colour(t.food/100 + 0.1, t.food/100 + 0.1, t.food/100 + 0.1)
    ShapeRenderer.renderCircle(t.shape, RenderParams(filled = true, colour = colour))

    Render.withContext(Frame(_colour = Colour.BLACK, _rotation = t.rotation - Deg(90), _position = t.position)) {
      glBegin(GL_TRIANGLES)
        Render.point(Vect(-20, -20))
        Render.point(Vect(20, -20))
        Render.point(Vect(0, GameConstants.TANK_SIZE))
      glEnd()
    }

  }

}
