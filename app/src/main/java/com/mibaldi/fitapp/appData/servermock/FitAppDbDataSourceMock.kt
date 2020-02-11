package com.mibaldi.fitapp.appData.servermock

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Training

class FitAppDbDataSourceMock(): RemoteDataSource{
    override suspend fun getTrainings(): List<Training> =
        (1..10).map { Training(it,"t$it") }

}