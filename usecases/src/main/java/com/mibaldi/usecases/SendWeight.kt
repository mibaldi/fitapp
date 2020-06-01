package com.mibaldi.usecases

import com.mibaldi.data.repository.TrainingsRepository
import com.mibaldi.domain.Either
import com.mibaldi.domain.FitAppError
import com.mibaldi.domain.Weight

class SendWeight (private val repository: TrainingsRepository){
    suspend operator fun invoke(weight: Double): Either<FitAppError,Boolean>{
        return repository.sendWeight(weight)
    }
}
class GetWeights (private val repository: TrainingsRepository){
    suspend operator fun invoke(): Either<FitAppError,List<Weight>>{
        return repository.getWeights()
    }
}