package com.genesis.app.ui.bazaar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.BazaarRepository
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@HiltViewModel
class BazaarViewModel @Inject constructor(
    private val repository: BazaarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BazaarUiState())
    val uiState: StateFlow<BazaarUiState> = _uiState

    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
        fetchAssets()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(500L)
                .collect { query ->
                    fetchAssets(query)
                }
        }
    }

    fun onEvent(event: BazaarUiEvent) {
        when (event) {
            is BazaarUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
                searchQueryFlow.value = event.query
            }
            BazaarUiEvent.OnRefresh -> {
                fetchAssets(_uiState.value.searchQuery)
            }
            is BazaarUiEvent.OnAssetClicked -> {
                _uiState.value = _uiState.value.copy(selectedAsset = event.asset)
            }
            BazaarUiEvent.OnDismissDialog -> {
                _uiState.value = _uiState.value.copy(selectedAsset = null)
            }
            is BazaarUiEvent.OnPurchaseClicked -> {
                purchaseAsset(event.assetId, event.price)
            }
        }
    }

    private fun fetchAssets(@Suppress("UNUSED_PARAMETER") query: String = "") {
        viewModelScope.launch {
            repository.getAssets().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            assets = resource.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = resource.message
                        )
                    }
                }
            }
        }
    }

    private fun purchaseAsset(@Suppress("UNUSED_PARAMETER") assetId: String, @Suppress("UNUSED_PARAMETER") price: Int) {
        // Implement purchase logic with Repository
    }
}

data class BazaarUiState(
    val assets: List<CreationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val selectedAsset: CreationResponse? = null,
    val error: String? = null
)

sealed class BazaarUiEvent {
    data class OnSearchQueryChanged(val query: String) : BazaarUiEvent()
    object OnRefresh : BazaarUiEvent()
    data class OnAssetClicked(val asset: CreationResponse) : BazaarUiEvent()
    object OnDismissDialog : BazaarUiEvent()
    data class OnPurchaseClicked(val assetId: String, val price: Int) : BazaarUiEvent()
}
