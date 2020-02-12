package com.mibaldi.fitapp.appData.server

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FitAppDbService {
    @GET("")
    suspend fun listTrainingsAsync(): Response<FitAppDbResult>

    @GET("/trainings")
    suspend fun findById(@Query("id")id: Int): Response<Training>
}