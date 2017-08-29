package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.output.CSVLogger
import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Vect}
import com.vogonjeltz.machineInt.evoTanks.physics.collision.{CollisionManager, EdgeCollision, ObjectObjectCollision, QuadTree}
import com.vogonjeltz.machineInt.evoTanks.simulation.{Bullet, FoodDropping, Tank, VisionRay}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by Freddie on 06/08/2017.
  */
class Arena(
           val useGenerations: Boolean = false,
           val genCount: Int = 0,
           val runCode: String = System.currentTimeMillis().toString,
           _startingPopilation: List[Tank] = List()
           ) {

  val deadTanksSociety: ArrayBuffer[Tank] = new ArrayBuffer[Tank]()

  val width: Int = GameConstants.SIMULATION_ARENA_WIDTH
  val height: Int = GameConstants.SIMULATION_ARENA_HEIGHT

  val objects: ArrayBuffer[SimulationObject] = new ArrayBuffer[SimulationObject]()
  objects.appendAll(_startingPopilation)

  def tanks = objects.collect {
    case t: Tank => t
  }.toList

  val collisionManager: CollisionManager[SimulationObject] = new QuadTree(new Box(width, height, Vect(width/2, height/2)), 5, true)

  val updateSync: Sync[() => Unit, Unit] = new Sync[() => Unit, Unit](60, (x: Option[() => Unit]) => doUpdate(x.get))

  val tankCSVLogger = new CSVLogger(s"output/tanks-$runCode-$genCount.csv")
  tankCSVLogger.append("food,kids,kills,ticks,gage,shots,spawn")

  private def remove(o: SimulationObject): Unit = {
    if (objects contains o) objects -= o
    o match {
      case tank: Tank =>
        tankCSVLogger.append(
          tank.totalFoodEaten,
          tank.totalChildren,
          tank.totalKills,
          tank.totalTicks,
          tank.geneticAge,
          tank.totalShots,
          tank.spawnTick
        )
        tank.kill()
        if (useGenerations) {
          if (deadTanksSociety.map(_.score).sum / deadTanksSociety.length <= tank.score || deadTanksSociety.isEmpty) {
            deadTanksSociety.append(tank)
          }
        }
      case _ =>
    }
  }

  private def addObject(o: SimulationObject): Unit =
    if (!objects.contains(o) && collisionManager.shape.contains(o.shape.position)) o match {
      case t: Tank =>
        if (objects.count(_.isInstanceOf[Tank]) < 200)
          objects.append(t)
      case _ => objects.append(o)
    }

  private var _UPDCount = 0
  def UPDCount: Int = _UPDCount


  def update(f: () => Unit) = updateSync.call(Some(f))

  def doUpdate(f: () => Unit): Unit = {

    _UPDCount += 1

    val visionRays = tanks.flatMap(_.visionRays)

    val collisions =
      collisionManager.calculateCollisions(
        objects.toList.filter(_.layer == GameConstants.GAME_LAYER) ++ visionRays
      )
    //println(s"${collisions._1.length} object-object collision(s) this update")
    //println(s"${collisions._2.length} edge-object collision(s) this update")
    //collisions._1.foreach(X => println(s"${X.a.name} collides with ${X.b.name}"))

    (collisions._1.flatMap {
      case ObjectObjectCollision(v: VisionRay, o: SimulationObject) => List(ResolvableAction { arena =>
        if (v.owner != o && !o.isInstanceOf[VisionRay]) {
          //println("VISION")
          v.owner.see(o.colour, v.index)
        }
        List()
      })
      case ObjectObjectCollision(o: SimulationObject, v: VisionRay) => List(ResolvableAction { arena =>
        if (v.owner != o) {
          //println("VISION")
          v.owner.see(o.colour, v.index)
        }
        List()
      })
      case ObjectObjectCollision(a: Tank, b: Bullet) => Bullet.killAction(b,a)
      case ObjectObjectCollision(b: Bullet, a: Tank) => Bullet.killAction(b,a)
      case _ => List()
    } ++ collisions._2.flatMap {
      case EdgeCollision(t: Tank, _) =>
        t.teleport(Vect(Random.nextInt(width - 200) + 100, Random.nextInt(height - 200) + 100))
        List()
      case EdgeCollision(o: SimulationObject, _)=> List(RemoveObjectAction(o))
    } ++ ActionAggregator().aggregate(implicit a => {

      //Spawn food upto minimum food levels
      if (objects.count(_.layer == GameConstants.FOOD_LAYER) < GameConstants.SIMULATION_MAX_FOOD)
        Range(0,50).foreach(
          x => AddObjectAction(
            new FoodDropping(
              Vect(Random.nextInt(width), Random.nextInt(height)),
              Random.nextDouble() * 10 + 5)
          )(a))

      if (objects.count(_.isInstanceOf[Tank]) < GameConstants.SIMULATION_MIN_TANKS) {

        AddObjectAction(Tank.spawn(
          Vect(Random.nextInt(width - 200) + 100, Random.nextInt(height - 200) + 100),
          UPDCount
        ))

      } else if (UPDCount % 500 == 0) {
        AddObjectAction(Tank.spawn(
          Vect(Random.nextInt(width - 200) + 100, Random.nextInt(height - 200) + 100),
          UPDCount
        ))
      }


    })).foreach(A => resolveAction(A))


    objects.flatMap(_.update()).foreach(A => resolveAction(A))

    f()

  }

  def cleanUp(): Unit = {
    tankCSVLogger.close()
  }

  def resolveAction(a: Action): Unit = a match {
    case RemoveObjectAction(o) => remove(o)
    case AddObjectAction(o) => addObject(o)
    case ResolvableAction(f) => f(this).map(resolveAction _)
  }

}
