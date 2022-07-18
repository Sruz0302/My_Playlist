package com.sruz.myplaylist.application

import android.app.Application
import com.sruz.myplaylist.helpers.CustomToast
import com.sruz.myplaylist.helpers.Utils

class MyApplication : Application() {



    override fun onCreate() {
        super.onCreate()


        CustomToast.applicationReference(this)

        Utils.applicationReference(this)


    }


    companion object {
        fun getInstance() : MyApplication.Companion {
            return this
        }
    }


}