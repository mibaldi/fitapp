package com.mibaldi.fitapp.services

import android.app.Activity
import android.widget.Toast

class FirebaseAnalytics : Analytics {

    private var foregroundActivity: Activity? = null
    override fun activityCreated(activity: Activity) {
    }

    override fun activityResumed(activity: Activity) {
        foregroundActivity = activity

    }

    override fun activityPaused(activity: Activity) {
        foregroundActivity = null
    }

    override fun activityDestroyed(activity: Activity) {
    }

    override fun logEvent(event: String) {
        Toast.makeText(foregroundActivity,event,Toast.LENGTH_SHORT).show()
    }

    override fun logError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}