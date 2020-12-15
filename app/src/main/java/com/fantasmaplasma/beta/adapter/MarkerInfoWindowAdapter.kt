package com.fantasmaplasma.beta.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.fantasmaplasma.beta.R
import com.fantasmaplasma.beta.databinding.MarkerInfoOnMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(private val mContext: Context) : GoogleMap.InfoWindowAdapter {

    private var mWindow: MarkerInfoOnMapBinding? = null

    override fun getInfoWindow(p0: Marker?): View? {
        bindWindow(p0)
        return mWindow?.root
    }

    override fun getInfoContents(p0: Marker?): View? {
        bindWindow(p0)
        return mWindow?.root
    }

    @SuppressLint("InflateParams")
    private fun bindWindow(marker: Marker?) {
        mWindow = mWindow ?:
            MarkerInfoOnMapBinding.inflate(
                LayoutInflater.from(mContext)
            )
        mWindow?.apply {
            tvMarkerInfoName.text = marker?.title
            tvMarkerInfoGrade.text = marker?.snippet
        }
    }
}