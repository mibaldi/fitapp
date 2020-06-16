package com.mibaldi.fitapp.appData.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.appData.servermock.fromJson

class WorkoutListConverter {
    val gson = Gson()

    @TypeConverter
    fun stringToWorkoutList(workoutString: String?) : List<WorkoutEntity>{
        return workoutString?.let {
           gson.fromJson<List<WorkoutEntity>>(workoutString)
        } ?: emptyList()
    }

    @TypeConverter
    fun workoutListToString(workoutList: List<WorkoutEntity>) : String?{
        return gson.toJson(workoutList)
    }
}

class TagConverter {
    val gson = Gson()

    @TypeConverter
    fun stringToTag(tagString: String?) : TagEntity?{
        return tagString?.let {
            gson.fromJson<TagEntity>(tagString)
        }
    }

    @TypeConverter
    fun tagToString(tag: TagEntity) : String?{
        return gson.toJson(tag)
    }
}