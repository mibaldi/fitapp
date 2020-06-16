package com.mibaldi.fitapp.appData.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mibaldi.domain.Tag
import java.util.*

@Entity
@TypeConverters(DateConverter::class,WorkoutListConverter::class,TagConverter::class)
data class TrainingEntity(
    @PrimaryKey var id: String,
    val name: String,
    val date: Date,
    val circuit: String,
    val tags: List<TagEntity>,
    val workouts: List<WorkoutEntity>
)
