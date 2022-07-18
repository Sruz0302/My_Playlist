package com.sruz.myplaylist.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sruz.myplaylist.R
import java.io.File

@SuppressLint("StaticFieldLeak")
object Utils {
    private val TAG = this::class.java.simpleName
    private var context: Context? = null

    fun applicationReference(mContext: Context) {
        context = mContext
    }


    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var value = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            if (nw != null) {
                val actNw = connectivityManager.getNetworkCapabilities(nw)
                if (actNw != null) {
                    value = when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        //for other device how are able to connect with Ethernet
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                        //for check internet over Bluetooth
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                        else -> false
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            value = connectivityManager.activeNetworkInfo?.isConnected ?: false
        }

        if (!value) {
            Toast.makeText(
                context,
                context!!.getString(R.string.string_no_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        return value
    }




}