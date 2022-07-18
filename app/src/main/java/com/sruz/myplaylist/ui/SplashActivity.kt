package com.sruz.myplaylist.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sruz.myplaylist.BuildConfig
import com.sruz.myplaylist.BuildConfig.VERSION_CODE
import com.sruz.myplaylist.R
import com.sruz.myplaylist.databinding.ActivityMainBinding
import com.sruz.myplaylist.databinding.ActivitySplashBinding
import com.sruz.myplaylist.helpers.CustomToast

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val RECORD_REQUEST_CODE = 101
    private lateinit var mBinding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_splash
        )
        val versionCode: Int = BuildConfig.VERSION_CODE
        mBinding.tvVersionNumber.text="VersionCode: $versionCode"
        setupPermissions()

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }else{
            navigate()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    CustomToast.makeToast("You need to allow permission to access the features")
                } else {
                   navigate()
                }
            }
        }
    }

    private fun navigate() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                startActivity(Intent(this, MainActivity::class.java))

            finish()
        }, 2000)
    }
}