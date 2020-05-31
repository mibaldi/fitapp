package com.mibaldi.data.source

import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

interface RemoteDataSource {
    suspend fun getTrainings(): Either<FitAppError,List<Training>>
    suspend fun findById(trainingID: String): Either<FitAppError, Training>
    suspend fun uploadTraining():Either<FitAppError,Boolean>
    suspend fun getVideo(tag: String): String?
    suspend fun sendWeight(weight: Double): Either<FitAppError,Boolean>
}