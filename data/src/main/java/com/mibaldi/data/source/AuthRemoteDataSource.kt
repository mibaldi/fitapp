package com.mibaldi.data.source

import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight

interface AuthRemoteDataSource {
    suspend fun getUsers(): Either<FitAppError,List<String>>
    suspend fun registerUser(): Either<FitAppError,String>

}