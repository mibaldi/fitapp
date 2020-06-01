package com.mibaldi.fitapp.appData.server

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class FitAppDbResult(
    val page: Int,
    val result: List<Training>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

@Parcelize
data class Training(
    val id: Int,
    val name: String,
    val video: Boolean,
    val date: Date,
    val circuit: String,
    val tags: List<String>
) : Parcelable {
    constructor() : this(0, "", false, Date(), "", emptyList())

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