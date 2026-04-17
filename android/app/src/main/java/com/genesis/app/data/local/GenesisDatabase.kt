package com.genesis.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.genesis.app.data.local.dao.FeedDao
import com.genesis.app.data.local.entity.FeedEntity

import com.genesis.app.data.local.dao.CreationDao
import com.genesis.app.data.local.entity.CreationEntity

@Database(entities = [FeedEntity::class, CreationEntity::class], version = 1, exportSchema = false)
abstract class GenesisDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun creationDao(): CreationDao
}
