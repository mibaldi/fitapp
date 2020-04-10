package com.mibaldi.fitapp.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.TrainingsAdapter
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.PermissionRequester
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.detail.DetailActivity
import com.mibaldi.fitapp.ui.main.MainViewModel.UiModel
import com.mibaldi.fitapp.ui.place.PlaceActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel


class MainActivity : BaseActivity() {
    private lateinit var adapter: TrainingsAdapter
    private val viewModel: MainViewModel by lifecycleScope.viewModel(this)
    private val coarsePermissionRequester =
        PermissionRequester(this, ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = TrainingsAdapter(viewModel::onTrainingClicked)
        recycler.adapter = adapter
        MobileAds.initialize(this,getString(R.string.admob_value_debug))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.places, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_place -> {
                startActivity<PlaceActivity>{}
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigation.observe (this,Observer {event ->
            event.getContentIfNotHandled()?.let {
                startActivity<DetailActivity> {
                    putExtra(DetailActivity.TRAINING,it.id)
                }
            }
        })
    }
    override fun onNotificationReceived(intent: Intent) {
        super.onNotificationReceived(intent)
        Toast.makeText(this,"Mikel",Toast.LENGTH_SHORT).show()
    }

    private fun updateUi(model: UiModel) {

        progress.visibility = if (model is UiModel.Loading) View.VISIBLE else View.GONE

        when(model) {
            is UiModel.Content -> adapter.trainings = model.trainings
            UiModel.RequestLocationPermission -> coarsePermissionRequester.request {
                viewModel.onCoarsePermissionRequested()
            }
        }
    }
}
