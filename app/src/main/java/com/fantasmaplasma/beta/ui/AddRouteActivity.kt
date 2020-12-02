package com.fantasmaplasma.beta.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.esafirm.imagepicker.features.ImagePicker
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
        const val REQUEST_CODE_READ_PERMISSION = 100
        const val REQUEST_CODE_IMAGES = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initListeners()
        initImageRecyclerView()
    }

    private fun getLatLng() = LatLng(
        intent.getDoubleExtra(MapsActivity.EXTRA_LATITUDE, 0.0),
        intent.getDoubleExtra(MapsActivity.EXTRA_LONGITUDE, 0.0)
    )

    private fun initToolbar() {
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
                        throw Exception("Invalid route type of $type")
                }
            )
    }

    private fun initListeners() {
        with(binding) {

            sliderAddRouteDifficulty.addOnChangeListener { _, value, _ ->
                val betaScaleStr = getString(R.string.difficulty, value.toInt().toString())
                tvAddRouteDifficulty.text = betaScaleStr

                val conventionalGradeStr = "" //TODO
            }

            btnAddRouteSubmit.setOnClickListener {
                validateInput()
            }

        }
    }

    private fun validateInput() {
        with(binding) {
            val routeName = etAddRouteName.text
            etLayoutAddRouteName.error =
                when {
                    routeName?.length ?: 0 <= 0 ->
                        "Enter the name of the route."
                    routeName?.length!! > 40 ->
                        "Enter a shorter name."
                    else ->
                        ""
                }

            val fullBetaScaleStr = tvAddRouteDifficulty.text.toString()
            val betaScaleStr =
                fullBetaScaleStr.substring(
                    fullBetaScaleStr.indexOf(' ') + 1,
                    fullBetaScaleStr.indexOf('/')
                )
            if (betaScaleStr == "?") {
            }

            val routeHeight = etAddRouteHeight.text
            etLayoutAddRouteHeight.error =
                when {
                    routeHeight?.length ?: 0 <= 0 ->
                        "Make your best estimate for the height."
                    routeHeight!!.length > 5 ->
                        "Oxygen level too low at that height."
                    else ->
                        ""
                }

            val beta = etAddRouteBeta.toString()
            val locatingTips = etAddRouteLocation.toString()

            val isValid =
                etLayoutAddRouteName.error == "" &&
                betaScaleStr != "?" &&
                etLayoutAddRouteHeight.error == ""

            if(isValid) {
                finish()
            }
        }
    }

    private fun initImageRecyclerView() {
        mImageAdapter = ImageAdapter(this) {
            startIntentSelectImages()
        }
        binding.rvAddRouteImages.apply {
            adapter = mImageAdapter
            layoutManager =
                LinearLayoutManager(this@AddRouteActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun startIntentSelectImages() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_PERMISSION)
            return
        }
        ImagePicker.create(this)
            .limit(10)
            .includeAnimation(true)
            .apply {
                val selectedImages = ArrayList(mImageAdapter.mImage)
                if(selectedImages.isNotEmpty())
                    origin(selectedImages)
            }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            mImageAdapter.setImageList(
                ImagePicker.getImages(data)
            )
            binding.rvAddRouteImages.invalidate()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        enableMarker()
        super.onBackPressed()
    }

    private fun enableMarker() {
        MapsActivity.draggableRouteMarker?.apply {
            visibility = View.VISIBLE
            alpha = 1f
        }
    }

}