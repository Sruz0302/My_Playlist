package com.sruz.myplaylist.database.model


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist_table")
data class PlayList(val title: String,
                    val duration: String,
                    val isPlaying: Boolean,
                    val uri: String?=null,
                    @PrimaryKey(autoGenerate = true) val id: Int? = null
)
