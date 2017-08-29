package com.vogonjeltz.machineInt.evoTanks.physics

import java.beans.beancontext.BeanContext

/**
  * Created by Freddie on 09/07/2017.
  */
sealed abstract class Shape {

  def position: Vect

  //TODO: Refactor this??
  //TODO: Triangles
  def intersects(that: Shape): Boolean = (this, that) match {
    case (c1: Circle, c2: Circle) => Shape.circleCircleIntersects(c1, c2)

    case (b1: Box, c2: Circle) => Shape.boxCircleIntersects(b1, c2)
    case (c1: Circle, b2: Box) => Shape.boxCircleIntersects(b2, c1)

    case (b1: Box, b2: Box) => Shape.boxBoxIntersects(b1, b2)

    case (b1: Box, l1: LineSegment) => Shape.lineBoxIntersects(l1, b1)
    case (l1: LineSegment, b1: Box) => Shape.lineBoxIntersects(l1, b1)

    case (l1: LineSegment, c1: Circle) => Shape.lineCircleIntersects(l1, c1)
    case (c1: Circle, l1: LineSegment) => Shape.lineCircleIntersects(l1, c1)

    case (l1: LineSegment, l2: LineSegment) => Shape.lineLineIntersects(l1,l2)
  }

  def contains(p: Vect): Boolean
}

object Shape {

  /**
    * Returns true if two circles intersect
    * @param c1
    * @param c2
    * @return
    */
  def circleCircleIntersects(c1: Circle, c2: Circle):Boolean =
    (c1.position - c2.position).length <= (c1.radius + c2.radius)

  /**
    * Returns true if Box b1 intersects circle c2
    * @param b1
    * @param c2
    * @return
    */
  def boxCircleIntersects(b1:Box, c2: Circle): Boolean =
    //https://stackoverflow.com/a/402019/2329773
    //Circle centre contained within bounds of the box OR one of
    //the four edges intersects the circle
    b1.contains(c2.position)        ||
    lineCircleIntersects(b1.A, c2)  ||
    lineCircleIntersects(b1.B, c2)  ||
    lineCircleIntersects(b1.C, c2)  ||
    lineCircleIntersects(b1.D, c2)


  /**
    * Returns true if two boxes intersect
    * @param b1
    * @param b2
    * @return
    */
  def boxBoxIntersects(b1: Box, b2: Box): Boolean =
    b1.topLeft.x <= b2.bottomRight.x && b1.bottomRight.x >= b2.topLeft.x &&
    b1.topLeft.y <= b2.bottomRight.y && b1.bottomRight.y >= b2.topLeft.y

  def lineLineIntersects(l1: LineSegment, l2: LineSegment): Boolean = {
    //TODO
    val A1 = l1.p2.y - l1.p1.y
    val B1 = l1.p1.x - l1.p2.x
    val C1 = A1 * l1.p1.x + B1*l1.p1.y

    val A2 = l2.p2.y - l2.p1.y
    val B2 = l2.p1.x - l2.p2.x
    val C2 = A2 * l2.p1.x + B2*l2.p1.y

    val det  = A1 * B2 - A2 * B1

    if (det == 0) false
    else {
      val x = (B2 * C1 - B1 * C2) / det
      val y = (A1 * C2 - A2 * C1) / det

      x >= l1.minX && x <= l1.maxX && x >= l2.minX && x <= l2.maxX &&
        y >= l1.minY && y <= l1.maxY && y >= l2.minY && y <= l2.maxY
    }

  }

  def lineBoxIntersects(l1: LineSegment, b2: Box) : Boolean =
    b2.contains(l1.p1)  ||
    b2.contains(l1.p2)  ||
    lineLineIntersects(l1, b2.A) ||
    lineLineIntersects(l1, b2.B) ||
    lineLineIntersects(l1, b2.C) ||
    lineLineIntersects(l1, b2.D)

  def lineCircleIntersects(l1: LineSegment, c1: Circle) : Boolean = {
    //https://stackoverflow.com/a/1084899/2329773
    val d = l1.p1 - l1.p2
    val f = l1.p2 - c1.position


    //Equation solving
    val a = d dot d
    val b = 2 * (f dot d)
    val c = (f dot f) - Math.pow(c1.radius, 2)

    val discriminant = Math.pow(b,2) - (4 * a * c)

    if (discriminant < 0) false
    else {

      val t1 = (-b - Math.sqrt(discriminant)) / (2*a)
      val t2 = (-b + Math.sqrt(discriminant)) / (2*a)

      if (t1 >= 0 && t1 <= 1) true
      else if (t2 >= 0 && t2 <= 1) true
      else false

    }
  }



}

case class Circle(radius: Double, position: Vect) extends Shape {

  def contains(p: Vect) = (p - position).length < radius

}

case class LineSegment(p1: Vect, p2: Vect) extends Shape {

  //https://stackoverflow.com/a/328122/2329773
  override def contains(p: Vect): Boolean = {
    lazy val cp = (p.y - p1.y) * (p2.x - p1.x) - (p.x - p1.x) * (p2.y - p1.y)
    lazy val dp = (p.x - p1.x) * (p2.x - p1.x) + (p.y - p1.y)*(p2.y - p1.y)
    lazy val sl = (p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y)


    if (Math.abs(cp) > 0.0001) false
    else if (dp < 0) false
    else if (dp > sl) false
    else true

  }

  override def position = p1 + (p2 - p1)/2

  lazy val minX = List(p1.x, p2.x).min
  lazy val maxX = List(p1.x, p2.x).max

  lazy val minY = List(p1.y, p2.y).min
  lazy val maxY = List(p1.y, p2.y).max

}

class Box(val xLength: Double, val yLength: Double, val position: Vect) extends Shape {

  def contains(p: Vect) = {
    val sMB = (topLeft - p) dot (topLeft - topRight)
    val sBB = (topLeft - topRight).squared
    val sMD = (topLeft - p) dot (topLeft - bottomLeft)
    val sDD = (topLeft - bottomLeft).squared

    0 <= sMB &&
    sMB <= sBB &&
    0 <= sMD &&
    sMD <= sDD
  }

  /*def contains(p: Vect) =
    0 <= ((topLeft - p) dot (topLeft - topRight)) <= ((topLeft - topRight) dot (topLeft - topRight)) &&
    0 <= ((topLeft - p) dot (topLeft - bottomLeft)) <= */

  lazy val topLeft: Vect = position + Vect(-xLength/2, -yLength/2)
  lazy val topRight: Vect = position + Vect(xLength/2, -yLength/2)
  lazy val bottomLeft: Vect = position + Vect(-xLength/2, yLength/2)
  lazy val bottomRight: Vect = position + Vect(xLength/2, yLength/2)

  lazy val A: LineSegment = LineSegment(topLeft, topRight)
  lazy val B: LineSegment = LineSegment(topRight, bottomRight)
  lazy val C: LineSegment = LineSegment(bottomRight, bottomLeft)
  lazy val D: LineSegment = LineSegment(bottomLeft, topLeft)

  lazy val edges: List[LineSegment] = List(A,B,C,D)

}

object Box {

  def apply(xLength: Double, yLength: Double, position:Vect) = new Box(xLength, yLength, position)

  def apply(topLeft: Vect, bottomRight: Vect) =
    new Box(
      bottomRight.x - topLeft.x,
      bottomRight.y - topLeft.y,
      topLeft + Vect((bottomRight.x - topLeft.x) / 2, (bottomRight.y - topLeft.y) / 2)
    )

}

//TODO: Implement this
case class Triangle(p1: Vect, p2: Vect, p3: Vect) extends Shape {
  override def contains(p: Vect): Boolean = ???

  override def position: Vect = p1
}