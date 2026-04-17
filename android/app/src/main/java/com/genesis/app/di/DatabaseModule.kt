package com.genesis.app.di

import android.content.Context
import androidx.room.Room
import com.genesis.app.data.local.GenesisDatabase
import com.genesis.app.data.local.dao.FeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GenesisDatabase {
        return Room.databaseBuilder(
            context,
            GenesisDatabase::class.java,
            "genesis_db"
        ).build()
    }

    @Provides
    fun provideFeedDao(database: GenesisDatabase): FeedDao {
        return database.feedDao()
    }
}
