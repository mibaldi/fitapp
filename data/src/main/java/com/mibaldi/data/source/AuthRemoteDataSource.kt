package com.mibaldi.data.source

import com.mibaldi.domain.*

interface AuthRemoteDataSource {
    suspend fun getUsers(): Either<FitAppError,List<User>>
    suspend fun getUsers2(): Either<FitAppError,List<User>>
    suspend fun registerUser(): Either<FitAppError,String>

    suspend fun removeUserTrainings(userID:String): Either<FitAppError, String>
}