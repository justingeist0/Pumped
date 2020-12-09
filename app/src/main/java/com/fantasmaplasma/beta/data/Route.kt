package com.fantasmaplasma.beta.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class Route(private val latLng: LatLng,
            val name: String,
            val height: Int,
            val betaScale: Int,
            val userID: String,
            private val type: Int = BOULDERING
)
    : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return height.toString()
    }

    fun getType() = type

    companion object {
        const val BOULDERING = 0
        const val SPORT = 1
        const val TRAD = 2
        const val ALPINE = 3
    }

}