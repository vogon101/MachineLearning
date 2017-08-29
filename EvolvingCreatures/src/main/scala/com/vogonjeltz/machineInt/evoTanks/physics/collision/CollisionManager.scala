package com.vogonjeltz.machineInt.evoTanks.physics.collision

import com.vogonjeltz.machineInt.evoTanks.core.SimulationObject
import com.vogonjeltz.machineInt.evoTanks.physics.Shape

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by Freddie on 09/07/2017.
  */
abstract class CollisionManager[T <: Collideable] {

  def calculateCollisions(objects: List[T]): (List[ObjectObjectCollision[T, T]], List[EdgeCollision[T]])

  def debugRender(): Unit = {}

  def shape: Shape

}

class SimpleCollisionManager[T <: Collideable]() extends CollisionManager[T] {

  def shape: Shape = ???

  override def calculateCollisions(objects: List[T]): (List[ObjectObjectCollision[T, T]], List[EdgeCollision[T]]) = {
    val collisions: ListBuffer[ObjectObjectCollision[T,T]] = ListBuffer()
    for (i <- objects.indices) {
      for (j <- Range(i+1, objects.length)) {

        if (objects(i).shape.intersects(objects(j).shape) && objects(i).layer == objects(j).layer) {
          collisions.append(new ObjectObjectCollision(objects(i), objects(j)))
        }

      }
    }
    (collisions.toList, List())
  }

}