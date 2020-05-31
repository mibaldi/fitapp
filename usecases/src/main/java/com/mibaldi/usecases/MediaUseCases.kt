package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository

class GetVideoTraining(private val trainingsRepository: TrainingsRepository){
    suspend operator fun invoke(tag: String):String?{
        return trainingsRepository.getVideo(tag)
    }
}