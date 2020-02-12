package com.mibaldi.data.source

import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

interface RemoteDataSource {
    suspend fun getTrainings(): Either<FitAppError,List<Training>>
    suspend fun findById(id: Int): Either<FitAppError,Training>
}