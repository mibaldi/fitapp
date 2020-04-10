package com.mibaldi.fitapp

import android.app.Application
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.services.FirebaseAnalytics
import com.mibaldi.fitapp.ui.common.DialogManager
import org.koin.core.KoinComponent
import org.koin.core.get

class FitApp : Application(),KoinComponent{

    override fun onCreate() {
        super.onCreate()
        initDI()
        registerActivityLifecycleCallbacks(get<AnalyticsCallbacks>())
        registerActivityLifecycleCallbacks(get<DialogManager>())

    }
}