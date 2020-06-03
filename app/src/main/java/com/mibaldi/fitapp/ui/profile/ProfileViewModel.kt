package com.mibaldi.fitapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetWeights
import com.mibaldi.usecases.ImportTrainings
import com.mibaldi.usecases.SendWeight
import kotlinx.coroutines.launch
import org.koin.core.inject


class ProfileViewModel(private val sendWeightUseCase: SendWeight,private val importTrainings: ImportTrainings,private val getWeightUseCase: GetWeights) : ScopedViewModel(){
    fun sendWeight(first: Int, decimal: Int) {
        val weight = first+decimal*0.1
        launch {
            sendWeightUseCase(weight).foldT({

            },{

            })
        }
    }

    fun exportTrainings(trainings: List<Training>) {
        launch {
            importTrainings(trainings)
        }
    }
    private val _weights = MutableLiveData<List<Weight>>()
    val weights: LiveData<List<Weight>>
        get() {
            if (_weights.value == null) getWeights()
            return _weights
        }

    private fun getWeights() {
        launch {
            getWeightUseCase().foldT({},{
                _weights.value = it
            })
        }
    }

    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation

}