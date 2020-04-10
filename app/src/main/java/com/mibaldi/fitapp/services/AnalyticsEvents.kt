package com.mibaldi.fitapp.services

interface AnalyticsEvents {
    fun logEvent(event: String)
    fun logError(error: String)
}