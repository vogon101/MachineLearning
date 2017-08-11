package com.vogonjeltz.machineInt.evoTanks.physics.collision

import com.vogonjeltz.machineInt.evoTanks.core.SimulationObject
import com.vogonjeltz.machineInt.evoTanks.physics.LineSegment

/**
  * Collision
  *
  * Created by fredd
  */
sealed abstract class Collision {

  def isSameAs(c: Collision): Boolean

  override def equals(obj: scala.Any): Boolean = obj match {
    case x: Collision => isSameAs(x)
    case _ => false
  }

}

class ObjectObjectCollision[A <: Collideable,B <: Collideable](val a: A, val b: B) extends Collision {

  def isSameAs(c: Collision): Boolean =  c match {
    case c: ObjectObjectCollision[_,_] => (c.a == a && c.b == b) || (c.a == b && c.b == a)
    case _ => false
  }


}

object ObjectObjectCollision {

  def apply[A <: Collideable, B <: Collideable](a: A, b: B) = new ObjectObjectCollision(a,b)

  def unapply[A <: Collideable, B<:Collideable] (arg: ObjectObjectCollision[A,B]): Option[(A, B)] = Some((arg.a, arg.b))

}

class EdgeCollision[A <: Collideable](val a: A, val edge: LineSegment) extends Collision {

  override def isSameAs(c: Collision): Boolean = c match {
    case c: EdgeCollision[_] => c.a == a
    case _ => false
  }

}

object EdgeCollision {

  def unapply[A <: Collideable](arg: EdgeCollision[A]): Option[(A, LineSegment)] = Some(arg.a, arg.edge)

}