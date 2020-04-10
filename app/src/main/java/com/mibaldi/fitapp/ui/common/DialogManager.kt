package com.mibaldi.fitapp.ui.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import com.mibaldi.fitapp.R


class DialogManager : Application.ActivityLifecycleCallbacks {

    private var foregroundActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        foregroundActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {
        foregroundActivity = null
    }

    fun showDialog(onPossitiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        foregroundActivity?.let {
            dialog(it, onPossitiveClick, onNegativeClick)
        }
    }

    private fun dialog(
        activity: Activity,
        onPossitiveClick: () -> Unit,
        onNegativeClick: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Delete entry")
            .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
// The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(R.string.yes) { _, _ -> onPossitiveClick() }
            .setNegativeButton(R.string.no) { _, _ ->
                onNegativeClick()

            }
            .setIcon(R.drawable.ic_launcher_foreground)
            .show()
    }


    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStarted(activity: Activity) {}

}