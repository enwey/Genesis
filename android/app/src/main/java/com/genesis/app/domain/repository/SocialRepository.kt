package com.genesis.app.domain.repository

import com.genesis.app.data.model.FeedItem
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    suspend fun getFeed(): Flow<Resource<List<FeedItem>>>
    suspend fun likeAsset(assetId: String): Flow<Resource<Unit>>
    suspend fun favoriteAsset(assetId: String): Flow<Resource<Unit>>
}
