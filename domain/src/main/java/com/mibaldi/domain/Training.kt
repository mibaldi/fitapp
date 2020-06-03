package com.mibaldi.domain

import java.util.*

data class Training(
    val id: String,
    val name: String,
    val date: Date,
    val circuit: String,
    var tags: List<Tag> = emptyList(),
    var workoutList: List<Workout> = emptyList()
)