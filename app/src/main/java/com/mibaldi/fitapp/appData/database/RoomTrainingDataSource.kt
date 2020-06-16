package com.mibaldi.fitapp.appData.database

import com.mibaldi.data.source.LocalTrainingDataSource
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.toDomainTraining
import com.mibaldi.fitapp.appData.toTrainingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomTrainingDataSource (db: TrainingDatabase): LocalTrainingDataSource{

    private val trainingDao = db.trainingDao()

    override suspend fun isEmpty() = withContext(Dispatchers.IO) {
        trainingDao.trainingsCount() <= 0
    }

    override suspend fun saveTrainings(trainings: List<Training>) {
        withContext(Dispatchers.IO){
            trainingDao.insertTraining(trainings.map { it.toTrainingEntity() })
        }
    }

    override suspend fun update(training: Training) {
        withContext(Dispatchers.IO){
            trainingDao.updateTraining(training.toTrainingEntity())
        }
    }

    override suspend fun findById(id: String) = withContext(Dispatchers.IO) {
        trainingDao.findById(id).toDomainTraining()
    }

    override suspend fun getTrainings() = withContext(Dispatchers.IO) {
        trainingDao.getAll().map { it.toDomainTraining() }
    }

}