package com.genesis.app.domain.repository

import com.genesis.app.data.model.LoginRequest
import com.genesis.app.data.model.LoginResponse
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun register(request: com.genesis.app.data.model.RegisterRequest): Flow<Resource<LoginResponse>>
}
