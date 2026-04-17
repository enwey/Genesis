package com.genesis.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.genesis.app.data.local.entity.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_items ORDER BY timestamp DESC")
    fun getAllFeedItems(): Flow<List<FeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<FeedEntity>)

    @Query("DELETE FROM feed_items")
    suspend fun clearAll()
}
