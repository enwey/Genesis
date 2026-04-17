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

    private val _feedState = MutableStateFlow<Resource<List<FeedItem>>>(Resource.Loading())
    val feedState: StateFlow<Resource<List<FeedItem>>> = _feedState

    init {
        fetchFeed()
    }

    fun fetchFeed() {
        viewModelScope.launch {
            repository.getFeed().collect { _feedState.value = it }
        }
    }

    fun likeAsset(assetId: String) {
        viewModelScope.launch {
            repository.likeAsset(assetId).collect { /* Update local state if success */ }
        }
    }

    fun favoriteAsset(assetId: String) {
        viewModelScope.launch {
            repository.favoriteAsset(assetId).collect { /* Update local state if success */ }
        }
    }
}
