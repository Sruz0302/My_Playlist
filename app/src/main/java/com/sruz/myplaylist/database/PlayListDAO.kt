package com.sruz.myplaylist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sruz.myplaylist.database.model.PlayList
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListDAO {

    @Insert
    fun insert(playlist: PlayList)

    @Update
    fun update(playlist: PlayList)


    @Query("select * from playlist_table")
    fun getAllPlayList(): LiveData<List<PlayList>>

    @Query("SELECT * FROM playlist_table")
    fun getPlayList(): Flow<List<PlayList>>
}