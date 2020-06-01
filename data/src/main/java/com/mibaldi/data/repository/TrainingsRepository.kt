package com.mibaldi.data.repository

import com.mibaldi.data.source.RemoteDataSource
import com.mibaldi.domain.*
import java.util.HashMap

class TrainingsRepository (
    private val remoteDataSource: RemoteDataSource,
    private val regionRepository: RegionRepository
) {
    suspend fun getTrainings(): List<Training> {
       return remoteDataSource.getTrainings().foldT({
            emptyList()
        },{
            it
        })
    }

    suspend fun findById(id: Int): Either<FitAppError,Training> {
        return remoteDataSource.findById(id.toString())
    }

    suspend fun getTrainingsDates(): HashMap<String,List<Training>> {
        return remoteDataSource.getTrainings().foldT({
            generateHashmap(emptyList())
        },{
            generateHashmap(it)
        })
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

    suspend fun uploadTrainings() {
        remoteDataSource.uploadTraining()
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