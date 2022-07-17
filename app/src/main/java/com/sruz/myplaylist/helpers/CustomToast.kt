package com.sruz.myplaylist.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object CustomToast {
    private var context: Context? = null

    fun applicationReference(mContext: Context) {
        context = mContext
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


}