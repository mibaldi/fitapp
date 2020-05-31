package com.mibaldi.fitapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetTrainings
import com.mibaldi.usecases.ImportTrainings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.inject

class MainViewModel (private val getTrainings: GetTrainings,private val importTrainings: ImportTrainings): ScopedViewModel(){

    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _model = MutableLiveData<UiModel>()
    val model : LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }
    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation

    sealed class UiModel {
        object Loading : UiModel()
        data class Content(val trainings: List<Training>): UiModel()
        object RequestLocationPermission : UiModel()

    }

    private fun refresh() {
        _model.value = UiModel.RequestLocationPermission
    }
    fun onCoarsePermissionRequested() {
        launch {
            _model.value = UiModel.Loading
            _model.value = UiModel.Content(getTrainings.invoke())
        }
    }

    fun onTrainingClicked(training: Training){
        analyticsCallbacks.logEvent("training clicked")
        _navigation.value = Event(training)
    }

    fun refreshTrainings() {
        launch {
            importTrainings()
        }
    }
}