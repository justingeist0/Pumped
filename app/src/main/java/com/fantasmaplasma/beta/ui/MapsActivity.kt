package com.fantasmaplasma.beta.ui

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.fantasmaplasma.beta.adapter.MarkerClusterRenderer
import com.fantasmaplasma.beta.R
import com.fantasmaplasma.beta.data.Route
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.maps.android.clustering.ClusterManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMapView: View
    private lateinit var mContainer: RelativeLayout
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mClusterManager: ClusterManager<Route>
    private lateinit var mViewModel: MapsViewModel
    private var mMap: GoogleMap? = null

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val ZOOM_RANGE_NEW_ROUTE = 35f
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
        const val EXTRA_ROUTE_TYPE = "extra_route_type"
        const val RC_SIGN_IN = 100
        var draggableRouteMarker: View? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mMapView = findViewById(R.id.map)
        mContainer = findViewById(R.id.container_map)
        initViewModel()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        mViewModel.routesLiveData.observe(this) { clusterItems ->
            mClusterManager.addItems(clusterItems)
        }
    }

    override fun onResume() {
        super.onResume()
        draggableRouteMarker?.apply {
            if(visibility == View.INVISIBLE)
                removeNewRouteView()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            try {
                setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this@MapsActivity,
                        R.raw.map_style
                    )
                )
            } catch (e: Exception) {}
            setOnMapLongClickListener {
                showAddLocationDialog()
            }
            mClusterManager = ClusterManager(this@MapsActivity, this)
            setOnCameraIdleListener(mClusterManager)
            setOnMarkerClickListener(mClusterManager)
            mClusterManager.renderer =
                MarkerClusterRenderer(this@MapsActivity, this, mClusterManager)
        }
        mViewModel.requestRoutesData()
        requestLocationOnMap()
        mMap?.stopAnimation()
        mMap?.apply { animateCamera(CameraUpdateFactory.zoomTo(minZoomLevel)) }
    }

    private fun requestLocationOnMap() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                    .requestPermissions(
                        this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                break
            } else {
                mMap?.isMyLocationEnabled = true
                mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    moveMapOverLocation(location)
                }
            }
        }
    }

    private fun moveMapOverLocation(location: Location?) {
        location?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    requestLocationOnMap()
                    return
                }
            }
            Toast.makeText(this, getString(R.string.location_disabled), Toast.LENGTH_SHORT)
                .show()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_location_add -> {
                showAddLocationDialog()
                true
            }
            R.id.menu_satellite_map -> {
                mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.menu_normal_map -> {
                mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showAddLocationDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_location_add)

            val btnClickListener = View.OnClickListener {btn ->
                createDraggableMarker(
                    when (btn.id) {
                        R.id.btn_dialog_location_bouldering ->
                            Route.BOULDERING
                        R.id.btn_dialog_location_sport ->
                            Route.SPORT
                        R.id.btn_dialog_location_trad ->
                            Route.TRAD
                        R.id.btn_dialog_location_alpine ->
                            Route.ALPINE
                        else ->
                            throw Exception("Invalid id of ${btn.id} route in add location dialog.")
                    }
                )
                cancel()
            }
           findViewById<LinearLayout>(R.id.ll_dialog_location_btns)
               .children.iterator().forEach { btn ->
                   btn.setOnClickListener(btnClickListener)
               }

            show()
        }
    }

    private fun createDraggableMarker(routeType: Int) {
        if (mMap == null) {
            Toast.makeText(
                this,
                getString(R.string.load_map_before_adding_route),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        draggableRouteMarker?.removeNewRouteView()
        draggableRouteMarker =
            layoutInflater.inflate(R.layout.layout_map_new_route, mContainer, false)
                .apply {

                    addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                        x = mContainer.width / 2f - width / 2
                        y = mContainer.height / 2f - height / 2
                    }

                    setAddRouteMarkerTouchListener()

                    setAddRouteMarkerImage(routeType)

                    findViewById<View>(R.id.img_btn_route_remove)
                        .setOnClickListener {
                            it.removeNewRouteView()
                        }
                    findViewById<View>(R.id.img_btn_route_add)
                        .setOnClickListener {
                            val latLng = getLatLngFromLayoutCords(
                                x + width / 2f,
                                y + height
                            ) ?: return@setOnClickListener
                            animateMarkerToGoogleMarkerToAddRouteActivity(latLng, routeType)
                        }

                    mContainer.addView(
                        this,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    )
            }
    }

    private fun View.setAddRouteMarkerImage(routeType: Int) {
        findViewById<ImageView>(R.id.iv_new_route_type)
            .setImageDrawable(
                ContextCompat.getDrawable(
                    this@MapsActivity,
                    when (routeType) {
                        Route.BOULDERING ->
                            R.drawable.img_marker_boulder
                        Route.SPORT ->
                            R.drawable.img_marker_sport
                        Route.TRAD ->
                            R.drawable.img_marker_trad
                        Route.ALPINE ->
                            R.drawable.img_marker_alpine
                        else ->
                            throw Exception("Drawable does not exist for route.")
                    }
                )
            )
    }

    private fun View.setAddRouteMarkerTouchListener() {
        var startX = -1f
        var startY = -1f
        setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                }
                else -> {
                    val addX = event.x - startX
                    val addY = event.y - startY
                    x += addX
                    y += addY
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun View.removeNewRouteView() {
        mContainer.removeView(this)
        draggableRouteMarker = null
    }

    private fun getLatLngFromLayoutCords(mouseX: Float, mouseY: Float): LatLng? {
        val map = mMap ?: return null
        val smallestPossibleZoom = map.maxZoomLevel - ZOOM_RANGE_NEW_ROUTE
        if (map.cameraPosition.zoom <= smallestPossibleZoom) {
            Toast.makeText(this, getString(R.string.please_zoom_in), Toast.LENGTH_SHORT)
                .show()
            return null
        }
        return map.projection.fromScreenLocation(Point(mouseX.toInt(), mouseY.toInt()))
    }

    private fun animateMarkerToGoogleMarkerToAddRouteActivity(latLng: LatLng, routeType: Int) {
        mMap?.addMarker(
            MarkerOptions()
                .position(latLng)
        )
        draggableRouteMarker?.apply {
            animate()
                .alpha(0f)
                .setDuration(1000)
                .withEndAction {
                    visibility = View.INVISIBLE
                    startAddRouteActivity(latLng, routeType)
                }
                .start()
        }
    }

    private fun startAddRouteActivity(latLng: LatLng, routeType: Int) {
        startActivity(
            Intent(this, AddRouteActivity::class.java).apply {
                putExtra(EXTRA_LATITUDE, latLng.latitude)
                putExtra(EXTRA_LONGITUDE, latLng.longitude)
                putExtra(EXTRA_ROUTE_TYPE, routeType)
            }
        )
    }

    private fun logIn() : Boolean {
        val providers = mutableListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
            } else {

            }
        }
    }

}