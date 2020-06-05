package com.mibaldi.fitapp.ui.auth

import android.nfc.tech.NfcA
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetUsers
import com.mibaldi.usecases.ImportTrainings
import com.mibaldi.usecases.RegisterUser
import kotlinx.coroutines.launch
import org.koin.core.inject

class FirebaseViewModel(private val registerUserUseCase: RegisterUser) : ScopedViewModel(){
    fun registerUser() {
        launch {
            registerUserUseCase().foldT({},{
                _navigation.value = Event(Navigation.Main)
            })
        }
    }

    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Navigation>>()
    val navigation: LiveData<Event<Navigation>> = _navigation

    sealed class Navigation {
        object Main : Navigation()
    }
}