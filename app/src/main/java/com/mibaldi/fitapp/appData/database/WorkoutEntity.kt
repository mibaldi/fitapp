package com.mibaldi.fitapp.appData.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkoutEntity(
    @PrimaryKey var id: String,
    val entrenamiento:Long = 5000L,//1'20''
    val relajamiento: Long = 2000L,//10''
    val descanso: Long = 7000L,//1'
    val repeticiones : Int = 3,
    val series: Int = 2,
    var name: String=""
)