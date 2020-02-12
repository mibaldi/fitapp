package com.mibaldi.fitapp

import android.app.Application

class FitApp : Application(){

    override fun onCreate() {
        super.onCreate()
        initDI()
    }
}