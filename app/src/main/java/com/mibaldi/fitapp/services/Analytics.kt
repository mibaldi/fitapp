package com.mibaldi.fitapp.services

import android.app.Activity

interface Analytics {
    fun activityCreated(activity: Activity)
    fun activityResumed(activity: Activity)
    fun activityPaused(activity: Activity)
    fun activityDestroyed(activity: Activity)
    fun logEvent(event: String)
    fun logError(error: String)
}