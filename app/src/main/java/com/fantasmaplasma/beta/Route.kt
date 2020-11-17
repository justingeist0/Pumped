package com.fantasmaplasma.beta

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class Route(private val mPosition: LatLng,
            private val mTitle: String,
            private val mSnippet: String,
            private val mType: Int = BOULDERING)
    : ClusterItem {

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String? {
        return mTitle
    }

    override fun getSnippet(): String? {
        return mSnippet
    }

    companion object {
        const val BOULDERING = 0
        const val SPORT = 1
        const val TRAD = 2
        const val ALPINE = 3
    }

}