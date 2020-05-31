package com.mibaldi.fitapp.ui.training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetTrainings
import com.mibaldi.usecases.GetTrainingsHashMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject


class TrainingViewModel (private val getTrainingsHashMap: GetTrainingsHashMap): ScopedViewModel(){

    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation


    private val _trainings = MutableLiveData<HashMap<String,List<Training>>>()
    val trainings: LiveData<HashMap<String,List<Training>>>
        get() {
        if (_trainings.value == null) getAllTrainings()
        return _trainings
    }

    fun getAllTrainings(){
        launch {
            _trainings.value = getTrainingsHashMap()
        }
    }
    fun onTrainingClicked(training: Training){
        _navigation.value = Event(training)
    }
}