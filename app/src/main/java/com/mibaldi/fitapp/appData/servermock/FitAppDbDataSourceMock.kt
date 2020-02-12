package com.mibaldi.fitapp.appData.servermock

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

class FitAppDbDataSourceMock: RemoteDataSource{
    override suspend fun getTrainings(): Either<FitAppError,List<Training>> =
        Either.Right((0..10).map { Training(it,"t$it") })

    override suspend fun findById(id: Int): Either<FitAppError, Training> =
        Either.Right(Training(id,"T$id"))
}