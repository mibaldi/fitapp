package com.mibaldi.fitapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Tag
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.FindTrainingById
import kotlinx.coroutines.launch
import org.koin.core.inject

class DetailViewModel(
    private val trainingId: String,
    private val findTrainingById: FindTrainingById) : ScopedViewModel() {
    private val analyticsCallbacks by inject<AnalyticsCallbacks>()

    sealed class UiModel {
        data class Content(val training: Training): UiModel()
        data class Error(val error: String): UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) findTraining()
            return _model
        }

    private fun findTraining() = launch {
        findTrainingById.invoke(trainingId).foldT({
            _model.value = UiModel.Error(it.message ?: "Error general")
        },{
            _model.value = UiModel.Content(it)
        })
    }

}