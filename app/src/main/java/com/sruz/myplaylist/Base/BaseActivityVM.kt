package com.sruz.myplaylist.Base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.sruz.myplaylist.callbacks.ActivityVMCallback

open class BaseActivityVM : ViewModel(){
    /*Start Activity Elements*/
    protected val vmActivityCB: MutableLiveData<ActivityVMCallback> by lazy {
        MutableLiveData<ActivityVMCallback>()
    }

    fun getActivityCB(): LiveData<ActivityVMCallback> {
        return vmActivityCB
    }

    fun clearObservers(vmObserver: Observer<ActivityVMCallback>) {
        vmActivityCB.removeObserver(vmObserver)
        vmActivityCB.value = ActivityVMCallback.showProgressBar(false)
    }


}