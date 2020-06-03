package com.mibaldi.fitapp.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.domain.Weight
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetUsers
import com.mibaldi.usecases.GetWeights
import com.mibaldi.usecases.ImportTrainings
import com.mibaldi.usecases.SendWeight
import kotlinx.coroutines.launch
import org.koin.core.inject


class AdminViewModel(private val importTrainings: ImportTrainings,private val getUsers: GetUsers) : ScopedViewModel(){

    fun exportTrainings(trainings: List<Training>) {
        launch {
            importTrainings(trainings)
        }
    }
    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation

}