package com.mibaldi.fitapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.FindTrainingById
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class DetailViewModel(
    private val trainingId: Int,
    private val findTrainingById: FindTrainingById,
    override val uiDispatcher: CoroutineDispatcher
) : ScopedViewModel(uiDispatcher) {

    sealed class UiModel {
        data class Content(val training: Training): UiModel()
        data class Error(val error: String): UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) findMovie()
            return _model
        }

    private fun findMovie() = launch {
        findTrainingById.invoke(trainingId).foldT({
            _model.value = UiModel.Error(it.message ?: "Error general")
        },{
            _model.value = UiModel.Content(it)
        })
    }
}