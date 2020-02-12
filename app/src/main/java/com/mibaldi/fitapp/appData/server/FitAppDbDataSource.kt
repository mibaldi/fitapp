package com.mibaldi.fitapp.appData.server

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.toDomainTraining

class FitAppDbDataSource(private val fitAppDb: FitAppDb): RemoteDataSource {
    override suspend fun getTrainings(): Either<FitAppError,List<Training>> =
        with (fitAppDb.service.listTrainingsAsync()) {
            if (isSuccessful && body() != null) {
                Either.Right(body()!!.result.map { it.toDomainTraining() })
            } else  Either.Left(FitAppError(code(),"Error"))
        }

    override suspend fun findById(id: Int): Either<FitAppError, Training> =
        with (fitAppDb.service.findById(id)) {
            if (isSuccessful && body() != null) {
                Either.Right(body()!!.toDomainTraining())
            } else Either.Left(FitAppError(code(),"Error"))
        }

}