package com.mibaldi.fitapp.ui.common

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.KoinComponent

abstract class ScopedViewModel(uiDispatcher: CoroutineDispatcher): ViewModel(), Scope by Scope.Impl(uiDispatcher),KoinComponent {
    init {
        initScope()
    }

    @CallSuper
    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }
}