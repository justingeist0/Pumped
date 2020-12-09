package com.fantasmaplasma.beta.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fantasmaplasma.beta.data.Route
import com.fantasmaplasma.beta.utilities.Cloud
import com.firebase.ui.auth.AuthUI

class MapsViewModel : ViewModel() {

    private val _routeListLiveData = MutableLiveData<MutableList<Route>>()
    val routesLiveData: LiveData<MutableList<Route>>
        get() = _routeListLiveData

    fun requestRoutesData() {
        Cloud.downloadRouteClusterItems { routes ->
            _routeListLiveData.postValue(routes)
        }
    }

/*    private fun logIn() : Boolean {
        val providers = mutableListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            MapsActivity.RC_SIGN_IN
        )
        return true
    }*/

}