package com.sruz.myplaylist.database.model


import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.FieldPosition


@Entity(tableName = "playlist_table")
data class PlayList(val title: String,
                    val duration: String,
                    val isPlaying: Boolean,
                    val uri: String?=null,
                    @PrimaryKey(autoGenerate = true) val id: Int? = null
)
