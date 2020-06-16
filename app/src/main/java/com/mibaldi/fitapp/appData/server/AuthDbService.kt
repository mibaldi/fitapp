package com.mibaldi.fitapp.appData.server

import com.mibaldi.domain.FitAppError
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthDbService {

    @DELETE("/admin/trainings/{userId}")
    suspend fun deleteUser(@Path("userId")id: String): Response<FitAppError>
}