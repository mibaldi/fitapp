package com.mibaldi.fitapp.ui.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.mibaldi.domain.Address
import com.mibaldi.domain.Training
import com.mibaldi.fitapp.services.AnalyticsCallbacks
import com.mibaldi.fitapp.ui.common.DialogManager
import com.mibaldi.fitapp.ui.common.Event
import com.mibaldi.fitapp.ui.common.ScopedViewModel
import com.mibaldi.usecases.GetTrainings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.inject

class PlaceViewModel (uiDispatcher: CoroutineDispatcher): ScopedViewModel(uiDispatcher),
    PlaceSelectionListener {

    private val _model = MutableLiveData<UiModel>()
    val model : LiveData<UiModel>
        get() = _model

    sealed class UiModel {
        data class Content(val address: Address): UiModel()
    }

    override fun onPlaceSelected(place: Place) {
        val address = Address()
        place.addressComponents?.let {
            it.asList().forEach { component ->
                component.types.apply {
                    when {
                        this.contains("route") -> address.street = component.name
                        this.contains("street_number") -> address.streetNumber = component.name
                        this.contains("postal_code") -> address.postalCode = component.name
                        this.contains("country") -> address.country = component.shortName
                        this.contains("administrative_area_level_2") -> address.region = component.name
                        this.contains("locality") -> address.locality = component.name
                    }
                }
            }
        }
        _model.value = UiModel.Content(address)

    }

    override fun onError(p0: Status) {

    }
}