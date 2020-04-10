package com.mibaldi.fitapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.mibaldi.fitapp.R
import java.io.Serializable

class PlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        getAddress()
    }
    private fun getAddress() {
        Places.initialize(this,"AIzaSyDBZHWIXKkPkCMi2-eC5x4i8iJnmo8UTpY")
        val autoCompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS_COMPONENTS))
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
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

                Log.d("PLACE",address.toString())
            }

            override fun onError(status: Status) {
                Log.d("PLACE",status.toString())
            }

        })
    }


}

data class Address(var street: String?= "",
                   var locality: String?= "",
                   var postalCode: String?="",
                   var region: String?="",
                   var country: String?="",
                   var streetNumber: String?="")
