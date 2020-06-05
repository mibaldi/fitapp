package com.mibaldi.data.source

import com.mibaldi.domain.*

interface AuthRemoteDataSource {
    suspend fun getUsers(): Either<FitAppError,List<User>>
    suspend fun registerUser(): Either<FitAppError,String>

}