package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Training

class ImportTrainings(private val trainingsRepository: TrainingsRepository){
    suspend operator fun invoke(list:List<Training>)= trainingsRepository.uploadTrainings(list)
}