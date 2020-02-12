package com.mibaldi.fitapp.ui.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mibaldi.fitapp.R
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {
    companion object {
        const val TRAINING = "DetailActivity:training"
    }
    private val viewModel: DetailViewModel by currentScope.viewModel(this) {
        parametersOf(intent.getIntExtra(TRAINING,-1))
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: DetailViewModel.UiModel) {
        when (model){
            is DetailViewModel.UiModel.Content -> {
                with(model.training){

                    trainingDetailToolbar.title = name
                    trainingDetailSummary.text = name
                    trainingDetailInfo.setTraining(this)

                    val icon = if (name.contains("0",true))
                        R.drawable.ic_favorite_on else R.drawable.ic_favorite_off
                    trainingDetailFavorite.setImageDrawable(getDrawable(icon))
                }
            }
            is DetailViewModel.UiModel.Error -> {
                makeToast(model.error)
                finish()
            }
        }
    }

    private fun makeToast(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
    }
}
