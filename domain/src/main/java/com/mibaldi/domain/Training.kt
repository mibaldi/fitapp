package com.mibaldi.domain

import java.util.*

data class Training(
    val id: Int,
    val name: String,
    val date: Date,
    val circuit: String
){
    init {

    }
}