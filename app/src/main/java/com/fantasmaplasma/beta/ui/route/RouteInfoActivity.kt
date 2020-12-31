package com.fantasmaplasma.beta.ui.route

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fantasmaplasma.beta.databinding.ActivityRouteInfoBinding

class RouteInfoActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRouteInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRouteInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}