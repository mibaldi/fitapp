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
    val title: String,
    val video: Boolean,
    val date: Date,
    val circuit: String
) : Parcelable