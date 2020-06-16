package com.mibaldi.fitapp.appData.server

import com.mibaldi.domain.FitAppError
import kotlinx.android.parcel.Parcelize
import retrofit2.Response
import retrofit2.http.*

interface FitAppDbService {

    @DELETE("/api/admin/trainings/{userID}")
    suspend fun deleteUser(@Header("x-auth")auth: String,@Path("userID") userID: String): Response<ResultAuth>

    @GET("/api/admin/users")
    suspend fun getUser(@Header("x-auth")auth: String): Response<List<ServerUser>>
}