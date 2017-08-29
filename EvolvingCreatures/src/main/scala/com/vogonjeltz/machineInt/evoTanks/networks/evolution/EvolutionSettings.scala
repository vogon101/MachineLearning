package com.vogonjeltz.machineInt.evoTanks.networks.evolution

/**
  * Created by Freddie on 29/03/2017.
  */
case class EvolutionSettings(
                              populationSize: Int = 50,
                              tickTime: Int = 3,
                              generationSize: Int = 10,
                              evolutionDelta: Double = 50,
                              harshness: Int = 3,
                              geneticDiversityQuota: Int = 5
                            )
{

}
