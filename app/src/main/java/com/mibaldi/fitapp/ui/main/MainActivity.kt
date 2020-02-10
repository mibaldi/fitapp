package com.mibaldi.fitapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.adapter.TrainingsAdapter
import com.mibaldi.fitapp.ui.common.startActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.mibaldi.fitapp.ui.main.MainViewModel.UiModel
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: TrainingsAdapter
    private val viewModel: MainViewModel by currentScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = TrainingsAdapter(viewModel::onTrainingClicked)
        recycler.adapter = adapter
        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigation.observe (this,Observer {event ->
            event.getContentIfNotHandled()?.let {
                startActivity<MainActivity> {
                    //putExtra("TRAINING",it)
                }
            }
        })
    }

    private fun updateUi(model: UiModel) {

        progress.visibility = if (model is UiModel.Loading) View.VISIBLE else View.GONE

        when(model) {
            is UiModel.Content -> adapter.trainings = model.trainings
        }
    }
}
