package com.mibaldi.usecases

import com.mibaldi.data.repository.AuthRepository
import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Training

class GetUsers(private val authRepository: AuthRepository){
    suspend operator fun invoke(): Either<FitAppError, List<String>> = authRepository.getUsers()
}