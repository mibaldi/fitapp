package com.mibaldi.fitapp.ui.place

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.mibaldi.domain.Address
import com.mibaldi.fitapp.R
import com.mibaldi.fitapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_place.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class PlaceActivity : BaseActivity() {
    private val viewModel: PlaceViewModel by lifecycleScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        viewModel.model.observe(this, Observer(::updateUi))
        initializePlaces()
        getAddress()
    }
    private fun initializePlaces(){
        Places.initialize(this,"AIzaSyDBZHWIXKkPkCMi2-eC5x4i8iJnmo8UTpY")
    }
    private fun getAddress() {
        val autoCompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS_COMPONENTS))
        autoCompleteFragment.setOnPlaceSelectedListener(viewModel)
    }

    private fun updateUi(model: PlaceViewModel.UiModel) {
        when(model) {
            is PlaceViewModel.UiModel.Content -> setupAddress(model.address)
        }
    }

    private fun setupAddress(address: Address){
        address.apply {
            tvStreet.text = street
            tvStreetNumber.text = streetNumber
            tvLocality.text = locality
            tvPostalCode.text = postalCode
            tvRegion.text = region
            tvCountry.text = country
        }
    }


}
