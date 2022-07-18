package com.sruz.myplaylist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sruz.myplaylist.database.model.PlayList

@Database(entities = [PlayList::class], version = 1)
abstract class PlayListDatabase: RoomDatabase() {

    abstract fun playListDao(): PlayListDAO

    companion object {
        private var instance: PlayListDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): PlayListDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, PlayListDatabase::class.java,
                    "playlist_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

            }
        }
    }
}