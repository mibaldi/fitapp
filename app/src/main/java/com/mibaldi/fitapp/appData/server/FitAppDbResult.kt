package com.mibaldi.fitapp.appData.server

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class FitAppDbResult(
    val page: Int,
    val result: List<ServerTraining>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

@Parcelize
data class ServerTraining(
    var id: String,
    val name: String,
    val date: Date,
    val circuit: String,
    val tags: List<String>,
    val workouts: List<ServerWorkout>
) : Parcelable {
    constructor() : this("","", Date(), "", emptyList(), emptyList())

}

@Parcelize
data class ServerWorkout(var id: String,
                         val entrenamiento:Long = 5000L,//1'20''
                         val relajamiento: Long = 2000L,//10''
                         val descanso: Long = 7000L,//1'
                         val repeticiones : Int = 3,
                         val series: Int = 2,
                         var name: String=""):Parcelable {
    constructor() : this("",0,0,0,0,0,"")
}

@Parcelize
data class Tag(
    val tag: String,val name: String,val url: String
) : Parcelable {
    constructor() : this("","","")
}

@Parcelize
data class Weight(
    val date: Date,val weight: Double) : Parcelable {
    constructor() : this(Date(),0.toDouble())
}

