package com.genesis.app.domain.repository

import com.genesis.app.data.model.WalletResponse
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    suspend fun getBalance(): Flow<Resource<WalletResponse>>
}
