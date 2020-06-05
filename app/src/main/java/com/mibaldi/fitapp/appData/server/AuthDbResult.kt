package com.mibaldi.fitapp.appData.server

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ServerUser(
    val id: String, val email: String) : Parcelable {
    constructor() : this("","")
}