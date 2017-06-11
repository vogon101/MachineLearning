package com.vogonjeltz.machineInt.lib.dl4jModels

import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

/**
  * Created by Freddie on 09/06/2017.
  */
abstract class MultiLayerModelDefinition {

  val seed: Int

  def conf: MultiLayerConfiguration

  def createModel(): MultiLayerNetwork

}
