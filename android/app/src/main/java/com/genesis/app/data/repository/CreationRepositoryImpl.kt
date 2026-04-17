package com.genesis.app.data.repository

import com.genesis.app.data.api.GenesisApiService
import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.CreationRepository
import com.genesis.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreationRepositoryImpl @Inject constructor(
    private val apiService: GenesisApiService
) : CreationRepository {
    override suspend fun generateAsset(request: CreationRequest): Flow<Resource<CreationResponse>> = flow {
        emit(Resource.Loading())
        
        // 1. Content Moderation Check (Production requirement)
        val isPromptSafe = checkModeration(request.prompt)
        if (!isPromptSafe) {
            emit(Resource.Error("Prompt contains inappropriate content."))
            return@flow
        }

        // 2. Proceed with generation
        try {
            val response = apiService.generate3DAsset(request)
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
    
    private suspend fun checkModeration(prompt: String): Boolean {
        // In production, this would call OpenAI's Moderation API or a dedicated backend route
        // e.g. return apiService.moderate(prompt).isSuccessful
        val blockList = listOf("nsfw", "violence", "hate")
        return !blockList.any { prompt.lowercase().contains(it) }
    }
}
