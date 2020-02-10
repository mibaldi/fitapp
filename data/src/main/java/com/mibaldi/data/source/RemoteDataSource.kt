package com.mibaldi.data.source

import com.mibaldi.domain.Training

interface RemoteDataSource {
    suspend fun getTrainings(): List<Training>
}