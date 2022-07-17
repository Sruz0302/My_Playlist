package com.sruz.myplaylist.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.sruz.myplaylist.database.PlayListDatabase
import com.sruz.myplaylist.database.model.PlayList
import com.sruz.myplaylist.database.model.PlayListDAO
import com.sruz.myplaylist.utils.subscribeOnBackground

class PlayListRepository (application: Application) {

    private lateinit var playListDao: PlayListDAO
    private lateinit var allPlayList: LiveData<List<PlayList>>

    private val database = PlayListDatabase.getInstance(application)

    init {
        playListDao = database.playListDao()
        allPlayList = playListDao.getAllPlayList()
    }

    fun insert(playList: PlayList) {
        subscribeOnBackground {
            playListDao.insert(playList)
        }
    }

    fun update(playList: PlayList) {
        subscribeOnBackground {
            playListDao.update(playList)
        }
    }


    fun getAllNotes(): LiveData<List<PlayList>> {
        return allPlayList
    }



}