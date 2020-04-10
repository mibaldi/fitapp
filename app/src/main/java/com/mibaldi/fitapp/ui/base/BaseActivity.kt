package com.mibaldi.fitapp.ui.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mibaldi.fitapp.services.NotificationDelegate
import com.mibaldi.fitapp.services.NotificationDelegateImpl
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

abstract class BaseActivity :AppCompatActivity(),NotificationDelegate by NotificationDelegateImpl()
{
    private val viewModel: BaseViewModel by currentScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: BaseViewModel.UiModel) {
        Log.d("MIKEL","updateUi")
    }

    override fun onPause() {
        this.delegateOnPause(this)
        super.onPause()
    }

    override fun onPostResume() {
        super.onPostResume()
        this.delegateOnPostResume(this)
        onReceiveFCM(intent,this)
    }

    open fun onNotificationReceived(intent: Intent) {
        Log.d("MIKEL","onNotificationReceived")
        viewModel.checkIsSuspended()
    }

}