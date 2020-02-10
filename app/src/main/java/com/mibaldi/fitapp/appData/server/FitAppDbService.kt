package com.mibaldi.fitapp.appData.server

import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface FitAppDbService {
     @GET("")
     fun listTrainingsAsync():Deferred<FitAppDbResult>
}