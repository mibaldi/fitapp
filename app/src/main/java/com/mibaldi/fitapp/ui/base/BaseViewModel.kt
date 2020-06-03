package com.mibaldi.fitapp.ui.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.FindTrainingById
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class BaseViewModel (uiDispatcher: CoroutineDispatcher
): ScopedViewModel(uiDispatcher) {

    private val _model = MutableLiveData<UiModel>()
    val model : LiveData<UiModel>
        get() {
            return _model
        }

    sealed class UiModel {
        object Loading : UiModel()
    }
}