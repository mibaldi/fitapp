package com.mibaldi.fitapp.appData

import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.domain.Workout
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