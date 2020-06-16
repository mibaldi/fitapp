package com.mibaldi.fitapp.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mibaldi.domain.Training
import com.mibaldi.domain.User
import com.mibaldi.domain.Weight
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.*
import kotlinx.coroutines.launch
import org.koin.core.inject


class AdminViewModel(private val importTrainings: ImportTrainings, private val getUsersUseCase: GetUsers, private val removeUserTrainings: RemoveUserTrainings) : ScopedViewModel(){

    fun exportTrainings(trainings: List<Training>) {
        launch {
            _userClicked.value?.let {
                importTrainings(trainings,it)
            }
        }
    }
    fun importTrainingsFunction(trainings: List<Training>,userId: String){
        launch {
            importTrainings(trainings,userId)
        }
    }

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() {
            if (_users.value == null) {
                getUsers()
            }
            return _users
        }

    private fun getUsers() {
        launch {
            getUsersUseCase().foldT({},{
                _users.value = it
            })
        }
    }


    private val _userClicked = MutableLiveData<String>()
    val userClicked: LiveData<String> = _userClicked
    private val analyticsCallbacks by inject<AnalyticsCallbacks>()
    private val dialogManager by inject<DialogManager>()

    private val _navigation = MutableLiveData<Event<Training>>()
    val navigation: LiveData<Event<Training>> = _navigation

    fun onUserClicked(user:User){
        _userClicked.value = user.id
    }
}