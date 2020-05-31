package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError

class SendWeight (private val repository: TrainingsRepository){
    suspend operator fun invoke(weight: Double): Either<FitAppError,Boolean>{
        return repository.sendWeight(weight)
    }
}