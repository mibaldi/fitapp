package com.mibaldi.data.repository

import com.mibaldi.data.source.AuthRemoteDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.*
import java.util.HashMap

class AuthRepository (private val remoteDataSource: AuthRemoteDataSource) {
    suspend fun getUsers(): Either<FitAppError, List<User>> {
        return remoteDataSource.getUsers()
    }

    suspend fun registerUser() : Either<FitAppError, String>{
        return remoteDataSource.registerUser()
    }


}