package com.mibaldi.data.source

import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight

interface RemoteDataSource {
    suspend fun getTrainings(): Either<FitAppError,List<Training>>
    suspend fun findById(trainingID: String): Either<FitAppError, Training>
    suspend fun uploadTraining(list: List<Training>, toWho: String?):Either<FitAppError,Boolean>
    suspend fun getVideo(tag: String): String?
    suspend fun sendWeight(weight: Double): Either<FitAppError,Boolean>
    suspend fun getWeights(): Either<FitAppError, List<Weight>>
}