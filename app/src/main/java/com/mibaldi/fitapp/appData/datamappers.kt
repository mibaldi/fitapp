package com.mibaldi.fitapp.appData

import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.domain.Workout
import com.mibaldi.fitapp.appData.database.TagEntity
import com.mibaldi.fitapp.appData.database.TrainingEntity
import com.mibaldi.fitapp.appData.database.WorkoutEntity
import com.mibaldi.fitapp.appData.server.ServerWorkout
import com.mibaldi.fitapp.appData.server.ServerTraining as ServerTraining
import com.mibaldi.fitapp.appData.server.Weight as ServerWeight

fun ServerTraining.toDomainTraining(tags:List<Tag>): Training =
    Training(
        id,
        name,
        date,
        circuit,
        tags,workouts.map { it.toDomainWorkout() }
    )
fun Training.toServerTraining(): ServerTraining =
    ServerTraining(id,name,date,circuit,tags.map { it.tag },workoutList.map {
        it.toServerWorkout()
    })
fun ServerWeight.toDomainWeight(): Weight =
    Weight(date,weight)

fun Workout.toServerWorkout(): ServerWorkout =
    ServerWorkout(id, entrenamiento, relajamiento, descanso, repeticiones, series, name)
fun ServerWorkout.toDomainWorkout(): Workout =
    Workout(id, entrenamiento = entrenamiento,
        relajamiento = relajamiento,
        descanso = descanso,
        repeticiones = repeticiones, series = series,
        name = name)

fun Training.toTrainingEntity() : TrainingEntity {
    return TrainingEntity(id,name,date,circuit,tags.map { it.toTagEntity() },workoutList.map { it.toWorkoutEntity() })
}
fun Workout.toWorkoutEntity() : WorkoutEntity {
    return WorkoutEntity(id,entrenamiento,relajamiento,descanso,repeticiones,series,name)
}

fun TrainingEntity.toDomainTraining() : Training {
    return Training(id,name,date,circuit, tags.map { it.toDomainTag() },workouts.map { it.toDomainWorkout() })
}
fun WorkoutEntity.toDomainWorkout() : Workout {
    return Workout(id, entrenamiento = entrenamiento,
        relajamiento = relajamiento,
        descanso = descanso,
        repeticiones = repeticiones, series = series,
        name = name)
}

fun TagEntity.toDomainTag() : Tag {
    return Tag(tag,name,url)
}

fun Tag.toTagEntity() : TagEntity {
    return TagEntity(tag,name,url)
}