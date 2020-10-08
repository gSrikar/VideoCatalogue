package com.gsrikar.videocatalogue.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gsrikar.videocatalogue.app.VideoApplication.Companion.appContext


@Database(entities = [FavEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "video-app.db"

        /**
         * @return instance of the database
         */
        var appDb: AppDb? = null
            @Synchronized get() {
                if (field == null) {
                    return Room
                        .databaseBuilder(appContext, AppDb::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return field
            }
    }

    abstract fun favDao(): FavDao

}
