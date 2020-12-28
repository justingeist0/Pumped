package com.fantasmaplasma.beta.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fantasmaplasma.beta.data.Route
import com.fantasmaplasma.beta.databinding.RouteDrawerClusterItemBinding
import com.google.maps.android.clustering.Cluster

class RouteClusterAdapter(private val mContext: Context, routeCluster: Cluster<Route>, private val routeItemOnClick: (Route) -> Unit) : RecyclerView.Adapter<RouteClusterAdapter.RouteHolder>() {

    val mRouteCollection = routeCluster.getItems().toTypedArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder =
        RouteHolder(
            RouteDrawerClusterItemBinding.inflate(
                LayoutInflater.from(mContext)
            )
        )

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        holder.bindViewHolder(position)
    }

    override fun getItemCount() =
        mRouteCollection.size

    inner class RouteHolder(private val clusterItem: RouteDrawerClusterItemBinding): RecyclerView.ViewHolder(clusterItem.root) {

        fun bindViewHolder(idx: Int) {
            clusterItem.apply {
                val route = mRouteCollection[idx]
                tvClusterItemName.text = route.name
                root.setOnClickListener {
                    routeItemOnClick(route)
                }
            }
        }

    }

}