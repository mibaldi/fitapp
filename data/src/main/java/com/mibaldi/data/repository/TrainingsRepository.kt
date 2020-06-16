package com.mibaldi.data.repository

import com.mibaldi.data.source.LocalTrainingDataSource
import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.*
import java.util.HashMap

class TrainingsRepository (
    private val localTrainingDataSource: LocalTrainingDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val regionRepository: RegionRepository
) {
    suspend fun getTrainings(): List<Training> {

        if (localTrainingDataSource.isEmpty()){
            val trainings = remoteDataSource.getTrainings()
            trainings.foldT({},{
                localTrainingDataSource.saveTrainings(it)
            })
        }
        return localTrainingDataSource.getTrainings()

     /*  return remoteDataSource.getTrainings().foldT({
            emptyList()
        },{
            it
        })*/
    }

    suspend fun findById(id: String): Either<FitAppError,Training> {
      return localTrainingDataSource.findById(id)?.let {
            Either.Right(it)
       } ?: run {
          remoteDataSource.findById(id).foldT({
              return@foldT Either.Left(FitAppError(404,"Error"))
          },{
              localTrainingDataSource.update(it)
              return@foldT Either.Right(it)
          })
       }
    }

    suspend fun getTrainingsDates(): HashMap<String,List<Training>> {
        if (localTrainingDataSource.isEmpty()){
            val trainings = remoteDataSource.getTrainings()
            trainings.foldT({
            },{
                localTrainingDataSource.saveTrainings(it)
            })
        }
       return generateHashmap(localTrainingDataSource.getTrainings())

    }
    private fun generateHashmap(list:List<Training>): HashMap<String,List<Training>>{
        val hashMap = hashMapOf<String,List<Training>>()
        for (training in list){
            val dateTraining = generateStringDate(training.date)
            val listInDate = hashMap[dateTraining]?.toMutableList() ?: mutableListOf()
            listInDate.add(training)
            hashMap[dateTraining] = listInDate
        }
        return hashMap
    }

    suspend fun uploadTrainings(
        list: List<Training>,
        toWho: String?
    ) {
        remoteDataSource.uploadTraining(list,toWho)
    }

    suspend fun getVideo(tag: String): String? {
        return remoteDataSource.getVideo(tag)
    }

    suspend fun sendWeight(weight: Double) :Either<FitAppError,Boolean> {
        return remoteDataSource.sendWeight(weight)
    }

    suspend fun getWeights(): Either<FitAppError, List<Weight>> {
        return remoteDataSource.getWeights()
    }
}