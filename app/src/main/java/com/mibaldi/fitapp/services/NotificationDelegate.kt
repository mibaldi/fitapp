package com.mibaldi.fitapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

interface NotificationDelegate {
    fun delegateOnPostResume(context: Context)
    fun delegateOnPause(context: Context)
    companion object {
        val ACTION_NOTIFICATION = "Notification"
    }
    fun onReceiveFCM(intent: Intent, context: Context)
}