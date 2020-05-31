package com.mibaldi.fitapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetTrainings
import com.mibaldi.usecases.ImportTrainings
import com.mibaldi.usecases.SendWeight
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject


class ProfileViewModel(private val sendWeightUseCase: SendWeight,private val importTrainings: ImportTrainings) : ScopedViewModel(){
    fun sendWeight(first: Int, decimal: Int) {
        val weight = first+decimal*0.1
        launch {
            sendWeightUseCase(weight).foldT({

            },{

            })
        }
    }

    fun exportTrainings() {
        launch {
            importTrainings()
        }
    }

    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation

}