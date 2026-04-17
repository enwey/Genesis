package com.genesis.app.domain.repository

import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow

interface CreationRepository {
    suspend fun generateAsset(request: CreationRequest): Flow<Resource<CreationResponse>>
}
