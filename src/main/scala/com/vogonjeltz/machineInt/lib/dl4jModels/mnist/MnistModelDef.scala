package com.vogonjeltz.machineInt.lib.dl4jModels.mnist

import com.vogonjeltz.machineInt.lib.dl4jModels.MultiLayerModelDefinition
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.{MultiLayerConfiguration, NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction

/**
  * Created by Freddie on 09/06/2017.
  */
class MnistModelDef(override val seed: Int) extends MultiLayerModelDefinition {

  val imageSize = 28

  override val conf:MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
    .seed(seed)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .iterations(1)
    .learningRate(0.006)
    .updater(Updater.NESTEROVS)
    .momentum(0.9)
    .regularization(true).l2(1e-4)
    .list()
    .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
      .nIn(imageSize * imageSize)
      .nOut(1000)
      .activation(Activation.RELU)
      .weightInit(WeightInit.XAVIER)
      .build())
    .layer(1, new DenseLayer.Builder()
      .nIn(1000)
      .nOut(300)
      .activation(Activation.TANH)
      .build())
    .layer(2, new DenseLayer.Builder()
      .nIn(300)
      .nOut(300)
      .build()
    )
    .layer(3, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
      .nIn(300)
      .nOut(10)
      .activation(Activation.SOFTMAX)
      .weightInit(WeightInit.XAVIER)
      .build())
    .pretrain(false).backprop(true) //use backpropagation to adjust weights
    .build()

  override def createModel() = {
    val m = new MultiLayerNetwork(conf)
    m.init()
    m
  }

}
