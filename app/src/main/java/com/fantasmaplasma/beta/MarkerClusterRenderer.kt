package com.fantasmaplasma.beta

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerClusterRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<Route>)
    : DefaultClusterRenderer<Route>(context, map, clusterManager) {

    override fun setOnClusterItemClickListener(listener: ClusterManager.OnClusterItemClickListener<Route>?) {
        super.setOnClusterItemClickListener(listener)
    }

    override fun onBeforeClusterItemRendered(item: Route, markerOptions: MarkerOptions) {
        markerOptions.apply {
            title(item.title)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.img_marker_boulder))
        }
    }
}