package com.fantasmaplasma.beta.ui.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fantasmaplasma.beta.databinding.ActivityRouteInfoBinding

const val ROUTE_JSON = "routeJSON"

class RouteInfoActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRouteInfoBinding
    private lateinit var routeJson: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRouteInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        actionBar?.title = intent.getStringExtra(ROUTE_JSON)
    }
}