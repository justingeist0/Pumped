package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoRenderer(private val mContext: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }


}