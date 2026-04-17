package com.genesis.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.genesis.app.data.local.dao.FeedDao
import com.genesis.app.data.local.entity.FeedEntity

@Database(entities = [FeedEntity::class], version = 1)
abstract class GenesisDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}
