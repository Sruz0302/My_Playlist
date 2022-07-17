package com.sruz.myplaylist.Base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.sruz.myplaylist.R
import com.sruz.myplaylist.callbacks.ActivityVMCallback
import com.sruz.myplaylist.helpers.Constants
import com.sruz.myplaylist.helpers.CustomToast
import com.sruz.myplaylist.helpers.ProgressDialogue

open class BaseActivity : FragmentActivity() {

    var mIntent: Intent? = null
    var mContext: Context? = null
    var mView: View? = null

    private val progressDialogue by lazy {
        ProgressDialogue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        mView = parent
        mContext = context
        return super.onCreateView(parent, name, context, attrs)
    }

    protected fun progressDialogueVisibility(isVisible: Boolean) {
        if (isVisible)
            progressDialogue.show()
        else
            progressDialogue.dismiss()
    }

    protected val vmCallbackObserver = Observer<ActivityVMCallback> { callBack ->
        mIntent = null

        when (callBack) {

//            ProgressBar Visibility Handing
            is ActivityVMCallback.ShowProgressBar -> {
                progressDialogueVisibility((callBack.isVisible))
            }

            is ActivityVMCallback.OnBackButtonPressed -> {
                onBackPressed()
            }

            is ActivityVMCallback.NoInternetAccess -> {
                CustomToast.makeToast(getString(R.string.string_no_internet))
            }

            is ActivityVMCallback.OnActivityChanged<*> -> {
                mIntent = Intent(this, callBack.cls)
            }

            is ActivityVMCallback.OnActivityChangedWithBundle<*> -> {
                mIntent = Intent(this, callBack.cls)
                mIntent!!.putExtra(Constants.BUNDLE_KEY, callBack.bundle)
            }

            is ActivityVMCallback.OnActivityChangedWithNewTask<*> -> {
                mIntent = Intent(this, callBack.cls)
                mIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }


        }

        if (mIntent != null) {
            startActivity(mIntent)
        }
    }



}