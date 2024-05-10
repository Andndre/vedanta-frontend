package com.example.brahmacarlearning.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.brahmacarlearning.data.remote.response.BabsItem

@Dao
interface BabDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(gita: List<BabsItem>)

    @Query("SELECT * FROM bab")
    fun getAllBab(): PagingSource<Int, BabsItem>

    @Query("DELETE FROM bab")
    suspend fun deleteAll()
}