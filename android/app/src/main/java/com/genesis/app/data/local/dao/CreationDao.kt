package com.genesis.app.data.local.dao

import androidx.room.*
import com.genesis.app.data.local.entity.CreationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreationDao {
    @Query("SELECT * FROM last_creations ORDER BY assetId DESC LIMIT 1")
    fun getLastCreation(): Flow<CreationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: CreationEntity)

    @Query("DELETE FROM last_creations")
    suspend fun clearAll()
}
