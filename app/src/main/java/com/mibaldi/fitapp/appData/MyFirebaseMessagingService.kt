package com.mibaldi.fitapp.appData

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mibaldi.fitapp.services.NotificationDelegate

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MIKEL","onMessageReceived")

        val intent = Intent(NotificationDelegate.ACTION_NOTIFICATION)
        sendBroadcast(intent)
    }

    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}
