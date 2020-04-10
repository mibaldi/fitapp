package com.mibaldi.fitapp.services

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AnalyticsCallbacks(
    val analytics: Analytics
) : Application.ActivityLifecycleCallbacks,AnalyticsEvents {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = analytics.activityCreated(activity)

    override fun onActivityResumed(activity: Activity) = analytics.activityResumed(activity)

    override fun onActivityPaused(activity: Activity) = analytics.activityPaused(activity)
    override fun onActivityDestroyed(activity: Activity) = analytics.activityDestroyed(activity)

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}
    override fun logEvent(event: String) {
        analytics.logEvent(event)
    }

    override fun logError(error: String) {
        analytics.logError(error)
    }


    // Other methods are empty
}