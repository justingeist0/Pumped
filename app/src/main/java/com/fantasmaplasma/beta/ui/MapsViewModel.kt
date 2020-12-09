package com.fantasmaplasma.beta.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fantasmaplasma.beta.data.Route
import com.fantasmaplasma.beta.utilities.Cloud

class MapsViewModel : ViewModel() {

    private val _routeListLiveData = MutableLiveData<MutableList<Route>>()
    val routesLiveData: LiveData<MutableList<Route>>
        get() = _routeListLiveData

    fun requestRoutesData() {
        Cloud.downloadRouteClusterItems { routes ->
            _routeListLiveData.postValue(routes)
        }
    }

}