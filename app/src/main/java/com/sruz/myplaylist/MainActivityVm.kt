package com.sruz.myplaylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sruz.myplaylist.Repository.PlayListRepository
import com.sruz.myplaylist.database.model.PlayList

class MainActivityVm (app: Application) : AndroidViewModel(app) {
    private val repository = PlayListRepository(app)
    private val allNotes = repository.getAllNotes()

    fun insert(playList: PlayList) {
        repository.insert(playList)
    }

    fun update(playList: PlayList) {
        repository.update(playList)
    }


    fun getAllNotes(): LiveData<List<PlayList>> {
        return allNotes
    }
}