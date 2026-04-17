package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.local.dao.FeedDao
import com.genesis.app.data.local.entity.toDomainModel
import com.genesis.app.data.local.entity.toEntity
import com.genesis.app.data.model.FeedItem
import com.genesis.app.domain.repository.SocialRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SocialRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService,
    private val feedDao: FeedDao
) : SocialRepository {

    override suspend fun getFeed(): Flow<Resource<List<FeedItem>>> = flow {
        emit(Resource.Loading())
        
        // 1. 发射本地缓存
        val localFeed = feedDao.getAllFeedItems().first().map { it.toDomainModel() }
        if (localFeed.isNotEmpty()) {
            emit(Resource.Success(localFeed))
        }

        try {
            // 2. 请求网络
            val response = apiService.getSocialFeed()
            if (response.isSuccessful) {
                val remoteFeed = response.body() ?: emptyList()
                // 3. 更新本地缓存
                feedDao.clearAll()
                feedDao.insertFeedItems(remoteFeed.map { it.toEntity() })
                
                // 4. 发射最新数据
                emit(Resource.Success(remoteFeed))
            } else {
                if (localFeed.isEmpty()) {
                    emit(Resource.Error(response.message()))
                }
            }
        } catch (e: Exception) {
            if (localFeed.isEmpty()) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun likeAsset(assetId: String): Flow<Resource<Unit>> = flow {
        try {
            val response = apiService.likeAsset(assetId)
            if (response.isSuccessful) emit(Resource.Success(Unit)) else emit(Resource.Error(response.message()))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun favoriteAsset(assetId: String): Flow<Resource<Unit>> = flow {
        try {
            val response = apiService.favoriteAsset(assetId)
            if (response.isSuccessful) emit(Resource.Success(Unit)) else emit(Resource.Error(response.message()))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}
