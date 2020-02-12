package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Training

class GetTrainings(private val trainingsRepository: TrainingsRepository){
    suspend fun invoke(): List<Training> = trainingsRepository.getTrainings()
}