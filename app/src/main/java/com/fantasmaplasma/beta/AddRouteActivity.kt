package com.fantasmaplasma.beta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.model.LatLng

class AddRouteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CANCELLED = "extra_cancelled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_route)
        val latLng = LatLng(
            intent.getDoubleExtra(MapsActivity.EXTRA_LATITUDE, 0.0),
            intent.getDoubleExtra(MapsActivity.EXTRA_LONGITUDE, 0.0)
        )
        setUpToolbar()
    }

    private fun setUpToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val type = intent.getIntExtra(MapsActivity.EXTRA_ROUTE_TYPE, 0)
        supportActionBar?.title = getString(R.string.add_route_activity_title,
                when(type) {
                    Route.BOULDERING ->
                        getString(R.string.bouldering)
                    Route.SPORT ->
                        getString(R.string.sport_climbing)
                    Route.TRAD ->
                        getString(R.string.trad)
                    Route.ALPINE ->
                        getString(R.string.alpine)
                    else ->
                        throw Exception("Invalid route type of $type in AddRouteActivity")
                }
            )


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MapsActivity.draggableRouteMarker?.apply {
            visibility = View.VISIBLE
            alpha = 1f
        }
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}