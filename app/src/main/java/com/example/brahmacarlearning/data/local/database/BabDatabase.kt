package com.example.brahmacarlearning.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.brahmacarlearning.data.remote.response.BabsItem

@Database(
    entities = [BabsItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class BabDatabase : RoomDatabase() {

    abstract fun babDao(): BabDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE:BabDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): BabDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BabDatabase::class.java, "bab_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}