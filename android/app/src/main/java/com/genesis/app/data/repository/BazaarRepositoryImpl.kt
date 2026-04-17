package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.BazaarRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BazaarRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService
) : BazaarRepository {
    override suspend fun getAssets(): Flow<Resource<List<CreationResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getBazaarAssets()
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
