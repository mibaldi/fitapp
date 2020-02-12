package com.mibaldi.data.source

interface LocationDataSource {
    suspend fun findLastRegion(): String?
}