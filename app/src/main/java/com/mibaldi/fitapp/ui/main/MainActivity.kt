package com.mibaldi.fitapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.mibaldi.fitapp.BuildConfig
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.admin.AdminActivity
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.profile.ProfileActivity
import com.mibaldi.fitapp.ui.training.TrainingActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (BuildConfig.DEBUG){
            MobileAds.initialize(this,getString(R.string.admob_value_debug))
        }else {
            MobileAds.initialize(this,getString(R.string.admob_value))
        }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        cvtraining.setOnClickListener {
            startActivity<TrainingActivity>{}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.places, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                if (BuildConfig.FLAVOR.equals("trainer")) {
                    startActivity<AdminActivity> {}
                } else {
                    startActivity<ProfileActivity> {}
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNotificationReceived(intent: Intent) {
        super.onNotificationReceived(intent)
        Toast.makeText(this,"Mikel",Toast.LENGTH_SHORT).show()
    }
}
