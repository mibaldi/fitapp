package com.mibaldi.fitapp.appData.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrainingEntity::class,WorkoutEntity::class,TagEntity::class], version = 3)
abstract class TrainingDatabase : RoomDatabase(){
    companion object {
        fun build(context: Context) = Room.databaseBuilder(
            context,
            TrainingDatabase::class.java,
            "training-db"
        ).build()
    }

    abstract fun trainingDao(): TrainingDao
}