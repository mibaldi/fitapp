package com.mibaldi.domain

import com.sun.corba.se.spi.orbutil.threadpool.Work
import java.io.Serializable

data class Workout(var id: String = "",
                   val calentamiento:Long = 2000L,//10''
                   val entrenamiento:Long = 5000L,//1'20''
                   val relajamiento: Long = 2000L,//10''
                   val descanso: Long = 7000L,//1'
                   val repeticiones : Int = 3,
                   val series: Int = 2,
                   var name: String="",
                   var currentRep: Int= 1,
                   var currentSet: Int= 1) :Serializable {

    val total: Long
        get() = generateList().map { it.time }.sum()

    var tiempoRestante = total
    fun entrenamientoToString() = entrenamiento.toWorkoutString(false)

    fun totalToString() = total.toWorkoutString(true)

    fun relajamientoToString() = relajamiento.toWorkoutString(false)
    fun descansoToString() = descanso.toWorkoutString(false)


    fun generateList(): ArrayList<WorkoutStatus>{
        val listResult = arrayListOf<WorkoutStatus>(WorkoutStatus.Calentamiento(calentamiento))
        val listRep = arrayListOf<WorkoutStatus>()
        val listSet = arrayListOf<WorkoutStatus>()
        for(i in 1..repeticiones){
            listRep.add(WorkoutStatus.Entrenamiento(entrenamiento))
            listRep.add(WorkoutStatus.Relajamiento(relajamiento))
        }
        for (i in 1..series){
            listSet.addAll(listRep)
            if (i != series){
                listSet.add(WorkoutStatus.Descanso(descanso))
            }
        }

        listResult.addAll(listSet)
        return listResult
    }

}
fun createWorkoutStatus(name:String,time: Long) : WorkoutStatus{
    return when (name){
        "Entrenamiento" -> WorkoutStatus.Entrenamiento(time)
        "Calentamiento" -> WorkoutStatus.Calentamiento(time)
        "Relajamiento" -> WorkoutStatus.Relajamiento(time)
        "Descanso" -> WorkoutStatus.Descanso(time)
        else -> WorkoutStatus.Entrenamiento(time)
    }
}

sealed class WorkoutStatus (val time: Long,val name: String){
    data class Entrenamiento(val time2:Long): WorkoutStatus(time2,"Entrenamiento")
    data class Calentamiento(val time2:Long): WorkoutStatus(time2,"Calentamiento")
    data class Relajamiento(val time2:Long): WorkoutStatus(time2,"Relajamiento")
    data class Descanso(val time2:Long): WorkoutStatus(time2,"Descanso")
}