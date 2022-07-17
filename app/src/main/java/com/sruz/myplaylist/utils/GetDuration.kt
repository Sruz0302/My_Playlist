package com.sruz.myplaylist.utils

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException
import java.lang.IllegalStateException


open class GetDuration {
    private val TAG = "AudioUtil"
    private val mDurationPlayer: MediaPlayer? = MediaPlayer()

    @Throws(IOException::class)
     fun getAudioDuration(fileName: String?): Long {
        var duration: Long = 0
        if (mDurationPlayer == null) {
            return duration
        } /*from   ww  w. j a  v  a  2 s .  com*/
        try {
            mDurationPlayer.reset()
            mDurationPlayer.setDataSource(fileName)
            mDurationPlayer.prepare()
            duration = mDurationPlayer.duration.toLong()
            mDurationPlayer.stop()
        } catch (e: IOException) {
            Log.e(TAG, "IOException:" + e.message)
            throw e
        } catch (e: IllegalStateException) {
            Log.e(TAG, "getAudioDuration start playing IllegalStateException")
            throw e
        }
        return duration
    }
}