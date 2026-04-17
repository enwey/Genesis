package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.model.LoginRequest
import com.genesis.app.data.model.LoginResponse
import com.genesis.app.domain.repository.AuthRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService
) : AuthRepository {
    override suspend fun login(request: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    override suspend fun register(request: com.genesis.app.data.model.RegisterRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { emit(Resource.Success(it)) } ?: emit(Resource.Error("Empty body"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}
