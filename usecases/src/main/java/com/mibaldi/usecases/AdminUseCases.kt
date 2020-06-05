package com.mibaldi.usecases

import com.mibaldi.data.repository.AuthRepository
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.User

class GetUsers(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Either<FitAppError, List<User>> {
       return authRepository.getUsers()
    }
}
class RegisterUser(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Either<FitAppError, String>{
        return authRepository.registerUser()
    }
}