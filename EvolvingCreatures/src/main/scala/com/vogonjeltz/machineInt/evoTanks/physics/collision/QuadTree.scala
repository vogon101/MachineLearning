package com.vogonjeltz.machineInt.evoTanks.physics.collision

import com.vogonjeltz.machineInt.evoTanks.core.SimulationObject
import com.vogonjeltz.machineInt.evoTanks.gfx.{Colour, RenderParams, ShapeRenderer}
import com.vogonjeltz.machineInt.evoTanks.physics.{Box, Vect}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * QuadTreeCollisionManager
  *
  * Created by fredd
  */
class QuadTree[T <: Collideable](val shape: Box, val levels: Int, val root: Boolean = false ) extends CollisionManager[T] {

  lazy val children: List[QuadTree[T]] = {
    List(
      new QuadTree[T](Box(shape.xLength/2, shape.yLength/2, shape.topLeft + Vect(shape.xLength/4, shape.yLength/4)), levels - 1),
      new QuadTree[T](Box(shape.xLength/2, shape.yLength/2, shape.topLeft + Vect(shape.xLength/4, 3 * shape.yLength/4)), levels - 1),
      new QuadTree[T](Box(shape.xLength/2, shape.yLength/2, shape.topLeft + Vect(3 * shape.xLength/4, shape.yLength/4)), levels - 1),
      new QuadTree[T](Box(shape.xLength/2, shape.yLength/2, shape.topLeft + Vect(3 * shape.xLength/4, 3 * shape.yLength/4)), levels - 1)
    )
  }

  var buckets: List[QuadTree[T]] = List()
  private var _emptyLastTick: Boolean = true
  def emptyLastTick = _emptyLastTick

  override def calculateCollisions(objects: List[T]): (List[ObjectObjectCollision[T, T]], List[EdgeCollision[T]]) = {

    //TODO: REFACTOR ALL OF THIS YOU LAZY ****
    (if (levels > 0) {
      if (objects.nonEmpty) {
        _emptyLastTick = false
        if (buckets.isEmpty) buckets = children

        val calc = buckets.map(X => new ArrayBuffer[T]()).zip(buckets)
        for (i <- objects) {
          for (b <- calc) {
            if (b._2.shape.intersects(i.shape)) b._1.append(i)
          }
        }
        calc.flatMap(X => X._2.calculateCollisions(X._1.toList)._1).distinct

      } else if (buckets.nonEmpty) {
        _emptyLastTick = true
        buckets = List()
        List()
      } else {
        _emptyLastTick = true
        List()
      }

    } else {
      val collisions: ArrayBuffer[ObjectObjectCollision[T, T]] = ArrayBuffer()
      val x = for (i <- objects.indices) {
        for (j <- Range(i+1, objects.length)) {
          if (objects(i).shape.intersects(objects(j).shape) && objects(i).layer == objects(j).layer)
            if (objects(i) > objects(j))
              collisions.append(new ObjectObjectCollision[T, T](objects(i), objects(j)))
            else
              collisions.append(new ObjectObjectCollision[T, T](objects(j), objects(i)))
        }
      }
      collisions.toList
    }, if (root) {
      val eCollisions = ListBuffer[EdgeCollision[T]]()
      for (o <- objects) {
        for (e <- shape.edges)
          if (e.intersects(o.shape)) eCollisions.append(new EdgeCollision[T](o, e))
      }
      eCollisions.toList
    } else List())

  }

  override def debugRender = {
    def renderQT(quadTree: QuadTree[_]): Unit = {

      val colour = quadTree.levels match {
        case 5 => Colour.DARK_GREEN
        case 4 => Colour.CYAN
        case 3 => Colour.YELLOW
        case 2 => Colour.GREEN
        case 1 => Colour.RED
        case 0 => Colour.BLUE
        case _ => Colour.WHITE
      }

      ShapeRenderer.renderBox(RenderParams(colour = colour, filled = false), quadTree.shape)
      if (quadTree.levels > 0) quadTree.buckets.foreach(renderQT _)

    }

    renderQT(this)
  }

}
