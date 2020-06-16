package com.mibaldi.fitapp.appData.database

import androidx.room.*

@Dao
interface TrainingDao {
    @Query("SELECT * FROM TrainingEntity")
    fun getAll(): List<TrainingEntity>

    @Query("SELECT * FROM TrainingEntity WHERE id = :id")
    fun findById(id: String): TrainingEntity

    @Query("SELECT COUNT(id) FROM TrainingEntity")
    fun trainingsCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTraining(trainings: List<TrainingEntity>)

    @Update
    fun updateTraining(training: TrainingEntity)
}
