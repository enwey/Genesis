package com.genesis.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.FeedItem
import com.genesis.app.domain.repository.SocialRepository
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState

    init {
        fetchFeed()
    }

    fun onEvent(event: SocialUiEvent) {
        when (event) {
            SocialUiEvent.OnRefresh -> fetchFeed()
            is SocialUiEvent.OnLikeClicked -> likeAsset(event.assetId)
            is SocialUiEvent.OnFavoriteClicked -> favoriteAsset(event.assetId)
        }
    }

    private fun fetchFeed() {
        viewModelScope.launch {
            repository.getFeed().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        feedItems = resource.data ?: emptyList(),
                        error = null
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = resource.message
                    )
                }
            }
        }
    }

    private fun likeAsset(assetId: String) {
        viewModelScope.launch {
            repository.likeAsset(assetId).collect {
                // Optimistic UI update can go here
            }
        }
    }

    private fun favoriteAsset(assetId: String) {
        viewModelScope.launch {
            repository.favoriteAsset(assetId).collect {
                // Optimistic UI update can go here
            }
        }
    }
}

data class SocialUiState(
    val isLoading: Boolean = false,
    val feedItems: List<FeedItem> = emptyList(),
    val error: String? = null
)

sealed class SocialUiEvent {
    object OnRefresh : SocialUiEvent()
    data class OnLikeClicked(val assetId: String) : SocialUiEvent()
    data class OnFavoriteClicked(val assetId: String) : SocialUiEvent()
}
