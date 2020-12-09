package com.fantasmaplasma.beta.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.esafirm.imagepicker.features.ImagePicker
import com.fantasmaplasma.beta.R
import com.fantasmaplasma.beta.adapter.ImageAdapter
import com.fantasmaplasma.beta.data.Route
import com.fantasmaplasma.beta.databinding.ActivityAddRouteBinding
import com.fantasmaplasma.beta.utilities.Cloud
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

class AddRouteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRouteBinding
    private lateinit var mImageAdapter: ImageAdapter

    companion object {
        const val REQUEST_CODE_READ_PERMISSION = 100
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

    private fun getRouteID() =
        intent.getIntExtra(MapsActivity.EXTRA_ROUTE_TYPE, 0)

    private fun initToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(
            R.string.add_route_activity_title,
                when(getRouteID()) {
                    Route.BOULDERING ->
                        getString(R.string.bouldering)
                    Route.SPORT ->
                        getString(R.string.sport)
                    Route.TRAD ->
                        getString(R.string.trad)
                    Route.ALPINE ->
                        getString(R.string.alpine)
                    else ->
                        throw Exception("Invalid route type of ${getRouteID()}")
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
            val routeName = etAddRouteName.text.toString()
            val nameErrorStr =
                when {
                    routeName.isEmpty() ->
                        "Enter the name of the route."
                    routeName.length > 40 ->
                        "Enter a shorter name."
                    else ->
                        ""
                }
            etLayoutAddRouteName.error = nameErrorStr

            val fullBetaScaleStr = tvAddRouteDifficulty.text.toString()
            val betaScaleStr =
                fullBetaScaleStr.substring(
                    fullBetaScaleStr.lastIndexOf(' ')+1,
                    fullBetaScaleStr.indexOf('/')
                )
            if (betaScaleStr == "?") {
                Toast.makeText(this@AddRouteActivity, "Estimate the grade.", Toast.LENGTH_SHORT)
                    .show()
            }

            val routeHeight = etAddRouteHeight.text.toString()
            val heightErrorStr =
                when {
                    routeHeight.isEmpty() ->
                        "Make your best estimate for the height."
                    routeHeight.length > 5 ->
                        "Oxygen level too low at that height."
                    else ->
                        ""
                }
            etLayoutAddRouteHeight.error = heightErrorStr

            Log.d("TAG", etLayoutAddRouteName.error.toString())
            Log.d("TAG", betaScaleStr)
            Log.d("TAG", etLayoutAddRouteHeight.error.toString())

            val isValid =
                nameErrorStr.isEmpty() &&
                heightErrorStr.isEmpty() &&
                betaScaleStr != "?"

            if(isValid) {
                val latLng = getLatLng()
                val betaScaleInt = Integer.parseInt(betaScaleStr)
                uploadRoute(
                    routeStandbyData = Cloud.createRouteStandByHashMap (
                        "",
                        routeName,
                        latLng.latitude,
                        latLng.longitude,
                        Integer.parseInt(routeHeight),
                        getRouteID(),
                        betaScaleInt
                    ),
                    nameData = Cloud.createCommentDataHashMap(
                        "",
                        routeName
                    ),
                    heightData = Cloud.createCommentDataHashMap(
                        "",
                        routeHeight
                    ),
                    betaScaleData = Cloud.createCommentDataHashMap(
                        "",
                        betaScaleInt
                    ),
                    commentData = Cloud.createCommentDataHashMap (
                        "",
                        "comment"
                    )
                )
                finish()
            } else {
                Toast.makeText(this@AddRouteActivity, "Invalid Input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadRoute(routeStandbyData: HashMap<String, Any>, nameData: HashMap<String, Any>,
                            heightData: HashMap<String, Any>, betaScaleData: HashMap<String, Any>, commentData: HashMap<String, Any>) {
        with(Cloud) {
            val routesRef = FirebaseFirestore.getInstance().collection(ROUTE)
            routesRef.document().apply {
                set(routeStandbyData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Standby Info") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(NAME)
                    .add(nameData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded ID") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(HEIGHT)
                    .add(heightData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Height") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(BETA_SCALE)
                    .add(betaScaleData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Scale") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }

                routesRef.document(id)
                    .collection(COMMENT_DATA)
                    .add(commentData)
                    .addOnSuccessListener { Log.d("TAG", "Uploaded Comment") }
                    .addOnFailureListener { Log.d("TAG", "Failed") }
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