package com.mibaldi.data.repository

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

class TrainingsRepository (
    private val remoteDataSource: RemoteDataSource,
    private val regionRepository: RegionRepository
) {
    suspend fun getTrainings(): List<Training> {
       return remoteDataSource.getTrainings().foldT({
            emptyList()
        },{
            it
        })
    }

    suspend fun findById(id: Int): Either<FitAppError,Training> {
        return remoteDataSource.findById(id)
    }
}