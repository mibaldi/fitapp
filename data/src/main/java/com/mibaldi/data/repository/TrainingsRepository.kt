package com.mibaldi.data.repository

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.Training

class TrainingsRepository (
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getTrainings(): List<Training> {
        return remoteDataSource.getTrainings()
    }
}