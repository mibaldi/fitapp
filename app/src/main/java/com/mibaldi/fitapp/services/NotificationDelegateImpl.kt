package com.mibaldi.fitapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.mibaldi.fitapp.services.NotificationDelegate.Companion.ACTION_NOTIFICATION
import com.mibaldi.fitapp.ui.base.BaseActivity


class NotificationDelegateImpl : NotificationDelegate {
    private var fcmNotificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MIKEL","onReceive broadcast")
            (context as BaseActivity).onNotificationReceived(intent)
        }
    }

    override fun delegateOnPause(context: Context) {
        Log.d("MIKEL","delegateOnPause")
        try {
            context.unregisterReceiver(fcmNotificationReceiver)
        } catch (e: Exception) {}
    }

    override fun delegateOnPostResume(context: Context) {
        Log.d("MIKEL","delegateOnPostResume")
        context.registerReceiver(fcmNotificationReceiver, IntentFilter(ACTION_NOTIFICATION))
    }

    override fun onReceiveFCM(intent:Intent,context: Context){
        if (!intent.getStringExtra("Usuario").isNullOrEmpty()){
            intent.apply {
                replaceExtras(Bundle())
                action = ""
                data = null
                flags = 0
            }
            fcmNotificationReceiver.onReceive(context,Intent())
        }
    }
}