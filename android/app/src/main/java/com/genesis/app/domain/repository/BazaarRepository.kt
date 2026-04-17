package com.genesis.app.domain.repository

import com.genesis.app.data.model.CreationResponse
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow

interface BazaarRepository {
    suspend fun getAssets(): Flow<Resource<List<CreationResponse>>>
}
