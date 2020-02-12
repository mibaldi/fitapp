package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

class FindTrainingById(private val trainingsRepository: TrainingsRepository){
    suspend fun invoke(id: Int): Either<FitAppError,Training> = trainingsRepository.findById(id)
}