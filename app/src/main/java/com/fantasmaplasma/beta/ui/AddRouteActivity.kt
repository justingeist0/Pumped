package com.fantasmaplasma.beta.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fantasmaplasma.beta.R
import com.fantasmaplasma.beta.adapter.ImageAdapter
import com.fantasmaplasma.beta.data.Route
import com.fantasmaplasma.beta.databinding.ActivityAddRouteBinding
import com.google.android.gms.maps.model.LatLng

class AddRouteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRouteBinding
    private lateinit var mImageAdapter: ImageAdapter

    companion object {
        const val EXTRA_CANCELLED = "extra_cancelled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbar()
        setUpViews()
    }

    private fun getLatLng() = LatLng(
        intent.getDoubleExtra(MapsActivity.EXTRA_LATITUDE, 0.0),
        intent.getDoubleExtra(MapsActivity.EXTRA_LONGITUDE, 0.0)
    )

    private fun setUpToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val type = intent.getIntExtra(MapsActivity.EXTRA_ROUTE_TYPE, 0)
        supportActionBar?.title = getString(
            R.string.add_route_activity_title,
                when(type) {
                    Route.BOULDERING ->
                        getString(R.string.bouldering)
                    Route.SPORT ->
                        getString(R.string.sport)
                    Route.TRAD ->
                        getString(R.string.trad)
                    Route.ALPINE ->
                        getString(R.string.alpine)
                    else ->
                        throw Exception("Invalid route type of $type in AddRouteActivity")
                }
            )
    }

    private fun setUpViews() {
        with(binding) {
            btnAddRouteSubmit.setOnClickListener {
                validateInput()
            }

            sliderAddRouteDifficulty.addOnChangeListener { _, value, _ ->
                val betaScaleStr = getString(R.string.difficulty, value.toInt().toString())
                etAddRouteBeta.setText(betaScaleStr)

                val conventionalScaleStr = "" //TODO
            }
        }
        setUpImageRecyclerView()
    }

    private fun setUpImageRecyclerView() {
        mImageAdapter = ImageAdapter(this) {
            startIntentSelectImages()
        }

        binding.rvAddRouteImages.apply {
            adapter = mImageAdapter

            layoutManager =
                LinearLayoutManager(this@AddRouteActivity, LinearLayoutManager.HORIZONTAL, false)

            setHasFixedSize(true)
        }
    }


    private fun validateInput() {
        with(binding) {
            val routeName = etAddRouteName.text
            etAddRouteName.error =
                when {
                    routeName?.length ?: 0 <= 0 ->
                        "Enter a name for the route."
                    routeName?.length!! > 40 ->
                        "It is a route name, not a poem."
                    else ->
                        ""
                }

            val fullBetaScaleStr = tvAddRouteDifficulty.text.toString()
            val betaScaleStr =
                fullBetaScaleStr.substring(
                    fullBetaScaleStr.indexOf(' ') + 1,
                    fullBetaScaleStr.indexOf('/')
                )
            if(betaScaleStr == "?") {
            }

            val routeHeight = etAddRouteHeight.text
            etAddRouteHeight.error =
                when {
                    routeHeight?.length ?: 0 <= 0 ->
                        "Make your best estimate for the height."
                    etAddRouteBeta.text!!.length > 5 ->
                        "Oxygen level is too low at that height."
                    else ->
                        ""
                }

            val beta = etAddRouteBeta.toString()
            val locatingTips = etAddRouteLocation.toString()

            val isValid =
                etAddRouteBeta.error == "" &&
                        betaScaleStr != "?" &&
                        etAddRouteHeight.error == ""

            if(isValid) {
                finish()
            }
        }
    }

    private fun startIntentSelectImages() {

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