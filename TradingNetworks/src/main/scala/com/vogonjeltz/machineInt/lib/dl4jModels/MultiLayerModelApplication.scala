package com.vogonjeltz.machineInt.lib.dl4jModels

import com.vogonjeltz.machineInt.lib.Serialise
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator

/**
  * Created by Freddie on 09/06/2017.
  */
abstract class MultiLayerModelApplication[T] {

  def model: MultiLayerNetwork

  def use(input: INDArray): (T, Array[Double])

  def doTraining(savePath: String, trainingData: DataSetIterator, saveInterval: Int = 5, maxIntervals: Int = 20) = {

    println(s"Training model for ${saveInterval * maxIntervals} epochs")
    for (i <- Range(0, maxIntervals)) {
      for (j <- Range(0, saveInterval)) {
        println(s"Epoch ${i * saveInterval + j}")
        model.fit(trainingData)
      }
      Serialise.save(savePath, model)
    }
  }

  def evaluate(testData: DataSetIterator, numClasses: Int): String = {
    val eval = new Evaluation(numClasses)
    model.doEvaluation(testData, eval)
    eval.stats
  }

}
