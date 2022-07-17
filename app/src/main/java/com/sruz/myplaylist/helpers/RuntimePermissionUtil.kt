package com.sruz.myplaylist.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
@SuppressLint("StaticFieldLeak")
object RuntimePermissionUtil {
    private val TAG = this::class.java.simpleName
    lateinit var context: Context

    fun applicationReference(mContext: Context) {
        context = mContext
    }

    fun hasPermissions(vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }
}