package com.mibaldi.fitapp.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.PlaceActivity
import com.mibaldi.fitapp.ui.adapter.TrainingsAdapter
import com.mibaldi.fitapp.ui.base.BaseActivity
import com.mibaldi.fitapp.ui.common.PermissionRequester
import com.mibaldi.fitapp.ui.common.startActivity
import com.mibaldi.fitapp.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.mibaldi.fitapp.ui.main.MainViewModel.UiModel
import org.koin.android.ext.android.inject
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: TrainingsAdapter
    private val viewModel: MainViewModel by currentScope.viewModel(this)
    private val coarsePermissionRequester =
        PermissionRequester(this, ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = TrainingsAdapter(viewModel::onTrainingClicked)
        recycler.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigation.observe (this,Observer {event ->
            event.getContentIfNotHandled()?.let {
                startActivity<PlaceActivity> {}
                /*startActivity<DetailActivity> {
                    putExtra(DetailActivity.TRAINING,it.id)
                }*/
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
