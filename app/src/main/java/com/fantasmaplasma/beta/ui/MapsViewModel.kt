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

    private lateinit var mRoutes: MutableList<Route>

    fun requestRoutesData(shouldForceRequest: Boolean = false) {
        val shouldDownload = shouldForceRequest || !this::mRoutes.isInitialized || mRoutes.size == 0
        if(shouldDownload)
            Cloud.downloadRouteClusterItems { routes ->
                mRoutes = routes
                _routeListLiveData.postValue(mRoutes)
            }
        else
            _routeListLiveData.postValue(mRoutes)
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