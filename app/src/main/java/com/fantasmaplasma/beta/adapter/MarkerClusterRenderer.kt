package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.fantasmaplasma.beta.R
import com.fantasmaplasma.beta.data.Route
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerClusterRenderer(private val mContext: Context, map: GoogleMap, clusterManager: ClusterManager<Route>)
    : DefaultClusterRenderer<Route>(mContext, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: Route, markerOptions: MarkerOptions) {
        val markerWidthPx = mContext.resources.getDimensionPixelSize(R.dimen.marker_width)
        val markerHeightPx = mContext.resources.getDimensionPixelSize(R.dimen.marker_height)
        val markerImage = BitmapDescriptorFactory.fromBitmap(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(mContext.resources,
                    when (item.getType()) {
                        Route.BOULDERING ->
                            R.drawable.img_marker_boulder
                        Route.ALPINE ->
                            R.drawable.img_marker_alpine
                        Route.SPORT ->
                            R.drawable.img_marker_sport
                        Route.TRAD ->
                            R.drawable.img_marker_trad
                        else ->
                            R.drawable.img_marker_sport
                    }
                ), markerWidthPx, markerHeightPx, false
            )
        )
        markerOptions.apply {
            title(item.title)
            icon(markerImage)
        }
    }

}