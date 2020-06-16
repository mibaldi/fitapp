package com.mibaldi.data.source

import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

interface LocalTrainingDataSource {
    suspend fun isEmpty() : Boolean
    suspend fun saveTrainings(trainings: List<Training>)
    suspend fun update(training: Training)
    suspend fun findById(id: String): Training?
    suspend fun getTrainings(): List<Training>
}