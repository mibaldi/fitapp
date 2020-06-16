package com.mibaldi.fitapp.appData.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagEntity(
    @PrimaryKey var tag: String,var name: String,var url: String
)