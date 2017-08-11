package com.vogonjeltz.machineInt.evoTanks.core

import com.vogonjeltz.machineInt.evoTanks.physics.Vect

import scala.collection.mutable.ArrayBuffer

/**
  * Action
  *
  * Created by fredd
  */
abstract class Action (implicit val actionAggregator: Option[ActionAggregator] = None) {

  actionAggregator.map(_.register(this))

}

case class ActionAggregator() {

  private val _actions: ArrayBuffer[Action] = ArrayBuffer()
  def register(action: Action):Unit = _actions.append(action)
  def actions:List[Action] =  _actions.toList

  def aggregate(f:(Option[ActionAggregator]) => Unit): List[Action] = {
    f(Some(this))
    actions
  }

}

case class RemoveObjectAction(o: SimulationObject) (implicit _aa: Option[ActionAggregator] = None) extends Action ()(_aa)

case class AddObjectAction(o: SimulationObject) (implicit _aa: Option[ActionAggregator] = None) extends Action ()(_aa)

case class ResolvableAction(f: (Arena) => List[Action]) (implicit _aa: Option[ActionAggregator] = None) extends Action ()(_aa)

object ResolvableAction {

  def apply(f: (Arena) => List[Action])(implicit _aa: Option[ActionAggregator] = None): ResolvableAction = new ResolvableAction(f)(_aa)

  //def apply(f: () => List[Action])(implicit _aa: Option[ActionAggregator] = None): ResolvableAction = new ResolvableAction((a: Arena) => f())(_aa)

}
