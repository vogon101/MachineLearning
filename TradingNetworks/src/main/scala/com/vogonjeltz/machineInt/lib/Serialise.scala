package com.vogonjeltz.machineInt.lib

import java.io.File

import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.util.ModelSerializer

/**
  * Created by Freddie on 09/06/2017.
  */
object Serialise {

  def save(path: String, model:Model) = {
    val location = new File(path)
    ModelSerializer.writeModel(model, location, true)
  }

  def read(path: String): MultiLayerNetwork = {
    ModelSerializer.restoreMultiLayerNetwork(path)
  }

}
