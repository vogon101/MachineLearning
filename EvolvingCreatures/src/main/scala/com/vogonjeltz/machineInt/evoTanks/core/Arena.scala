package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Vect}
import com.vogonjeltz.machineInt.evoTanks.physics.collision.{CollisionManager, EdgeCollision, ObjectObjectCollision, QuadTree}
import com.vogonjeltz.machineInt.evoTanks.simulation.{Bullet, FoodDropping, Tank}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by Freddie on 06/08/2017.
  */
class Arena(val width: Int, val height: Int) {

  val objects: ArrayBuffer[SimulationObject] = ArrayBuffer()

  val collisionManager: CollisionManager[SimulationObject] = new QuadTree(new Box(width, height, Vect(width/2, height/2)), 6, true)

  val updateSync: Sync[() => Unit, Unit] = new Sync[() => Unit, Unit](60, (x: Option[() => Unit]) => doUpdate(x.get))

  private def remove(o: SimulationObject) =
    if (objects contains o) objects -= o
  private def addObject(o: SimulationObject) =
    if (!objects.contains(o))objects.append(o)

  private var _UPDCount = 0
  def UPDCount: Int = _UPDCount


  def update(f: () => Unit) = updateSync.call(Some(f))

  def doUpdate(f: () => Unit): Unit = {

    _UPDCount += 1

    val collisions = collisionManager.calculateCollisions(objects.toList.filter(_.layer == GameConstants.GAME_LAYER))
    //println(s"${collisions._1.length} object-object collision(s) this update")
    //println(s"${collisions._2.length} edge-object collision(s) this update")
    //collisions._1.foreach(X => println(s"${X.a.name} collides with ${X.b.name}"))

    (collisions._1.flatMap {
      case ObjectObjectCollision(a: Tank, b: Tank) => List()
      case ObjectObjectCollision(a: Tank, b: Bullet) => Bullet.killAction(b,a)
      case ObjectObjectCollision(b: Bullet, a: Tank) => Bullet.killAction(b,a)
      case _ => List()
    } ++ collisions._2.flatMap {
      case EdgeCollision(t:SimulationObject, _) => List(RemoveObjectAction(t))
    } ++ ActionAggregator().aggregate(implicit a => {
      if (objects.count(_.isInstanceOf[FoodDropping]) < 1000)
        AddObjectAction(new FoodDropping(Vect(Random.nextInt(width), Random.nextInt(height)), Random.nextDouble() * 10))
      if (objects.count(_.isInstanceOf[Tank]) < 50) {
        if (UPDCount % 10 == 0) {
          val t1 = new Tank(Vect(Random.nextInt(5000), Random.nextInt(5000)))
          t1._velocity = Vect(Random.nextDouble() * 2 - 1, Random.nextDouble() * 2 - 1)

          AddObjectAction(t1)
        }
      }
    })).foreach(A => resolveAction(A))


    objects.flatMap(_.update()).foreach(A => resolveAction(A))

    f()

  }

  def resolveAction(a: Action): Unit = a match {
    case RemoveObjectAction(o) => remove(o)
    case AddObjectAction(o) => addObject(o)
    case ResolvableAction(f) => f(this).map(resolveAction _)
  }

}
