package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Training

class GetTrainings(private val trainingsRepository: TrainingsRepository){
    suspend operator fun invoke(): List<Training> = trainingsRepository.getTrainings()
}

class GetTrainingsHashMap(private val trainingsRepository: TrainingsRepository){
    suspend operator fun invoke(): HashMap<String,List<Training>> = trainingsRepository.getTrainingsDates()
}