package com.vogonjeltz.machineInt.lib.dl4jModels.mnist

import com.vogonjeltz.machineInt.lib.dl4jModels.MultiLayerModelDefinition
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.{LearningRatePolicy, MultiLayerConfiguration, NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.conf.layers.{ConvolutionLayer, DenseLayer, OutputLayer, SubsamplingLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction
import java.util.HashMap


/**
  * Created by Freddie on 09/06/2017.
  */
class MnistModelDef(override val seed: Int) extends MultiLayerModelDefinition {

  val imageSize = 28

  val lrSchedule = new HashMap[Integer, java.lang.Double]
  lrSchedule.put(0, 0.01)
  lrSchedule.put(1000, 0.005)
  lrSchedule.put(3000, 0.001)

  override val conf:MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
    .seed(seed)
    .iterations(1) // Training iterations as above
    .regularization(true).l2(0.0005)
    /*
        Uncomment the following for learning decay and bias
     */
    .learningRate(.01)//.biasLearningRate(0.02)
    /*
        Alternatively, you can use a learning rate schedule.

        NOTE: this LR schedule defined here overrides the rate set in .learningRate(). Also,
        if you're using the Transfer Learning API, this same override will carry over to
        your new model configuration.
    */
    .learningRateDecayPolicy(LearningRatePolicy.Schedule)
    .learningRateSchedule(lrSchedule)
    /*
        Below is an example of using inverse policy rate decay for learning rate
    */
    //.learningRateDecayPolicy(LearningRatePolicy.Inverse)
    //.lrPolicyDecayRate(0.001)
    //.lrPolicyPower(0.75)
    .weightInit(WeightInit.XAVIER)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .updater(Updater.NESTEROVS).momentum(0.9)
    .list()
    .layer(0, new ConvolutionLayer.Builder(5, 5)
      //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
      .nIn(1)
      .stride(1, 1)
      .nOut(20)
      .activation(Activation.IDENTITY)
      .build())
    .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
      .kernelSize(2,2)
      .stride(2,2)
      .build())
    .layer(2, new ConvolutionLayer.Builder(5, 5)
      //Note that nIn need not be specified in later layers
      .stride(1, 1)
      .nOut(50)
      .activation(Activation.IDENTITY)
      .build())
    .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
      .kernelSize(2,2)
      .stride(2,2)
      .build())
    .layer(4, new DenseLayer.Builder().activation(Activation.RELU).dropOut(0.5)
      .nOut(500).build())
    .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .nOut(10)
      .activation(Activation.SOFTMAX)
      .build())
    .setInputType(InputType.convolutionalFlat(28,28,1)) //See note below
    .backprop(true).pretrain(false).build()
    /*
    .seed(seed)
    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
    .iterations(1)
    .learningRate(0.006)
    .updater(Updater.NESTEROVS)
    .momentum(0.9)
    .regularization(true).l2(1e-4)
    .list()
    .layer(0, new DenseLayer.Builder()
      .nIn(imageSize * imageSize)
      .nOut(1300)
      .activation(Activation.RELU)
      .weightInit(WeightInit.XAVIER)
      .build())
    .layer(1, new DenseLayer.Builder()
      .nIn(1300)
      .nOut(300)
      .activation(Activation.RELU)
      .weightInit(WeightInit.XAVIER)
      .build())
    .layer(2, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
      .nIn(300)
      .nOut(10)
      .activation(Activation.SOFTMAX)
      .build())
    .pretrain(false).backprop(true) //use backpropagation to adjust weights
    .build()*/

  override def createModel() = {
    val m = new MultiLayerNetwork(conf)
    m.init()
    m
  }

}

