package com.mibaldi.fitapp.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.FindTrainingById
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class BaseViewModel (private val findTrainingById: FindTrainingById,uiDispatcher: CoroutineDispatcher): ScopedViewModel(uiDispatcher) {

    private val _model = MutableLiveData<UiModel>()
    val model : LiveData<UiModel>
        get() {
            return _model
        }

    sealed class UiModel {
        object Loading : UiModel()
    }

    fun checkIsSuspended(){
        launch {
            _model.value = UiModel.Loading
            findTrainingById(1)
        }
    }
}