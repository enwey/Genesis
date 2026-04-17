package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.model.WalletResponse
import com.genesis.app.domain.repository.WalletRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService
) : WalletRepository {
    override suspend fun getBalance(): Flow<Resource<WalletResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getWalletBalance()
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
}
