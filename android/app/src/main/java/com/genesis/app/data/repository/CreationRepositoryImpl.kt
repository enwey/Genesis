package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.CreationRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.genesis.app.data.local.dao.CreationDao
import com.genesis.app.data.local.entity.toDomainModel
import com.genesis.app.data.local.entity.toEntity
import kotlinx.coroutines.flow.map

class CreationRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService,
    private val creationDao: CreationDao
) : CreationRepository {
    override suspend fun generateAsset(request: CreationRequest): Flow<Resource<CreationResponse>> = flow {
        emit(Resource.Loading())

        val isPromptSafe = checkModeration(request.prompt)
        if (!isPromptSafe) {
            emit(Resource.Error("Prompt contains inappropriate content."))
            return@flow
        }

        try {
            val response = apiService.generate3DAsset(request)
            if (response.isSuccessful) {
                response.body()?.let { creationResponse ->
                    // Persistence: Save to local database
                    creationDao.insertCreation(creationResponse.toEntity())
                    emit(Resource.Success(creationResponse))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
        }
    }

    override fun getLastCreation(): Flow<CreationResponse?> {
        return creationDao.getLastCreation().map { it?.toDomainModel() }
    }

    private suspend fun checkModeration(prompt: String): Boolean {
        // In production, this would call OpenAI's Moderation API or a dedicated backend route
        val blockList = listOf("nsfw", "violence", "hate")
        return !blockList.any { prompt.lowercase().contains(it) }
    }
}
