package com.vogonjeltz.machineInt.evoTanks.simulation

import breeze.linalg.{DenseMatrix, DenseVector}
import com.vogonjeltz.machineInt.evoTanks.core._
import com.vogonjeltz.machineInt.evoTanks.gfx._
import com.vogonjeltz.machineInt.evoTanks.networks.NeuralNetwork
import com.vogonjeltz.machineInt.evoTanks.physics._
import org.lwjgl.opengl.GL11._

import scala.util.Random

/**
  * Created by Freddie on 06/08/2017.
  */
class Tank(protected var _position: Vect, val brain: TankBrain, val spawnTick: Int, val generation: Int, protected var _velocity: Vect = Vect.ZERO)
  extends SimulationObject with Renderable
{

  private var _totalFoodEaten = 0d
  private var _totalKills = 0
  private var _totalChildren = 0
  private var _totalShots = 0

  private var _isDead = false
  def kill():Unit = _isDead = true
  def isDead: Boolean = _isDead

  def totalFoodEaten: Double = _totalFoodEaten
  def totalKills: Int = _totalKills
  def totalTicks: Int = i
  def totalChildren: Int = _totalChildren
  def geneticAge: Int = brain.network.geneticAge
  def totalShots: Int = _totalShots

  def teleport(v: Vect): Unit = _position = v

  protected var _food: Double = GameConstants.TANK_STARTING_FOOD
  def food: Double = _food
  protected def eat(n: Double): Unit = {
    _totalFoodEaten += n
    _food += n
  }

  override val name = "Tank"

  val layer: Int = GameConstants.GAME_LAYER

  def position: Vect      = _position
  def velocity: Vect      = _velocity
  def rotation: Rotation  = _velocity.theta

  override def shape: Circle = Circle(GameConstants.TANK_SIZE, _position)

  private var i = 0
  private var _lastColour = Colour.WHITE
  override def colour: Colour = _lastColour
  def lifeSpan: Int = i

  private var _lastSeenColours: Array[Colour] = Array(Colour.BLACK,Colour.BLACK,Colour.BLACK,Colour.BLACK)
  def lastSeenColours: Array[Colour] = _lastSeenColours

  def score: Double =
    totalFoodEaten / 200d +
    food / 50d +
    totalChildren +
    totalKills * (if (!isDead) 100 else 4)+
    lifeSpan / 1000d +
    totalShots / 300d

  override def update(): List[Action] = ActionAggregator().aggregate { implicit a =>
    _food -= GameConstants.TANK_TICK_COST
    if (food < 0) RemoveObjectAction(this)

    val control = brain.update(
      DenseVector.tabulate(GameConstants.BRAIN_SIZE) {
        case 0  => (i % GameConstants.GENERATION_MAX_TIMER) / GameConstants.GENERATION_MAX_TIMER
        case 1  => food / 1000d
        case 2  => position.x / GameConstants.SIMULATION_ARENA_WIDTH.toDouble
        case 3  => position.y / GameConstants.SIMULATION_ARENA_HEIGHT.toDouble
        case 4  => velocity.x / GameConstants.TANK_MAX_SPEED
        case 5  => velocity.y / GameConstants.TANK_MAX_SPEED
        case 6  => lastSeenColours(0).r
        case 7  => lastSeenColours(0).g
        case 8  => lastSeenColours(0).b
        case 9  => lastSeenColours(1).r
        case 10 => lastSeenColours(1).g
        case 11 => lastSeenColours(1).b
        case 12 => lastSeenColours(2).r
        case 13 => lastSeenColours(2).g
        case 14 => lastSeenColours(2).b
        case 15 => lastSeenColours(3).r
        case 16 => lastSeenColours(3).g
        case 17 => lastSeenColours(3).b

        case _  => 0
      }
    )

    if (control.child) {
      if (food > GameConstants.TANK_CHILD_COST) {
        AddObjectAction(
          new Tank(
            _position + Vect(Random.nextDouble() * 200 - 100, Random.nextDouble() * 200 - 100),
            new TankBrain(brain.network.spawn(1, 20).head),
            i + spawnTick,
            generation
          )
        )
        _food -= GameConstants.TANK_CHILD_COST
        _totalChildren += 1
      } else {
        _food -= GameConstants.TANK_CHILD_FAIL_COST
      }
    }

    if (control.shoot) shoot()

    if (control.eat) eatAction()

    _velocity = velocity + Vect(control.deltaX, control.deltaY)
    if (velocity.length > GameConstants.TANK_MAX_SPEED)
      _velocity = Vect.fromAMF(velocity.theta, GameConstants.TANK_MAX_SPEED)

    _lastColour = control.colour

    _position = _position + velocity
    i += 1


    _lastSeenColours = Array(Colour.BLACK,Colour.BLACK,Colour.BLACK,Colour.BLACK)

  }

  def shoot()(implicit aa: Option[ActionAggregator]): Unit = {
    AddObjectAction(
      new Bullet(position, velocity * GameConstants.BULLET_SPEED_MULT, this)
    )
    _food -= GameConstants.BULLET_COST
    _totalShots += 1
  }

  def eatAction()(implicit aa: Option[ActionAggregator]): Unit = {
    ResolvableAction { arena =>

      arena.objects.filter(O => O.layer == GameConstants.FOOD_LAYER && O.shape.intersects(shape)).flatMap {
        case f: FoodDropping =>
          eat(f.size)
          List(RemoveObjectAction(f))
        case _ => List()
      }.toList match {
        case Nil =>
          _food -= 0
          List()
        case x: List[Action] =>
          x
      }

    }
  }

  def didKill(t: Tank): Unit = {
    eat(t.food)
    _totalKills += 1
  }

  override def render(): Seq[RenderPrimitive] = List (
    CircleRenderer(shape, RenderParams(filled = true, colour = colour)),
    TriangleRenderer(
      Triangle(Vect(-20, -20), Vect(20,-20), Vect(0, GameConstants.TANK_SIZE)),
      RenderParams(filled = true, rotation = rotation - Deg(90), offset = position, colour = Colour.WHITE)
    )
  ) ++ (
    if ((GameConstants.USE_GENERATIONS && generation > 1) || brain.network.geneticAge > 1)
      List(CircleRenderer(Circle(shape.radius + 5, position)))
    else List())

  def visionRays: List[VisionRay] = List(
    new VisionRay(
      position,
      position + Vect.fromAMF(rotation, GameConstants.TANK_VISION_RANGE),
      this,
      0
    ),
    new VisionRay(
      position,
      position + Vect.fromAMF(rotation + Deg(20), GameConstants.TANK_VISION_RANGE),
      this,
      1
    ),
    new VisionRay(
      position,
      position + Vect.fromAMF(rotation - Deg(20), GameConstants.TANK_VISION_RANGE),
      this,
      2
    ),
    new VisionRay(
      position,
      position + Vect.fromAMF(rotation - Deg(180), GameConstants.TANK_VISION_RANGE),
      this,
      3
    )
  )

  def see(colour: Colour, index:Int): Unit = {
    _lastSeenColours(index) = colour
  }


}


object Tank {

  def spawn(position: Vect, tick: Int): Tank = {
    val network = new NeuralNetwork(
      DenseMatrix.tabulate(GameConstants.BRAIN_SIZE, GameConstants.BRAIN_SIZE)((x,y) => Random.nextDouble() * 2 - 1d),
      DenseVector.tabulate(GameConstants.BRAIN_SIZE)(x => Random.nextDouble() * 2 - 1d),
      DenseVector.tabulate(GameConstants.BRAIN_SIZE)(x => Random.nextDouble() * 2 - 1d),
      GameConstants.BRAIN_ACTIVATION
    )
    new Tank(position, new TankBrain(network), tick, 0)
  }
}

/*
object TankRenderer {

  def renderTank(t: Tank): Unit = {
    ShapeRenderer.renderCircle(t.shape, RenderParams(filled = true, colour = t.colour))

    Render.withContext(Frame(_colour = Colour.WHITE, _rotation = t.rotation - Deg(90), _position = t.position)) {
      glBegin(GL_TRIANGLES)
        Render.point(Vect(-20, -20))
        Render.point(Vect(20, -20))
        Render.point(Vect(0, GameConstants.TANK_SIZE))
      glEnd()
    }

  }

}
*/