package com.mibaldi.fitapp.appData.server

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.toDomainTraining

class FitAppDbDataSource(private val fitAppDb: FitAppDb): RemoteDataSource {
    override suspend fun getTrainings(): List<Training> =
        fitAppDb.service.listTrainingsAsync().await().result.map { it.toDomainTraining() }

}