package com.genesis.app.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.CreationRepository
import com.genesis.app.data.websocket.CreationWebSocketClient
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val repository: CreationRepository,
    private val webSocketClient: CreationWebSocketClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreationUiState())
    val uiState: StateFlow<CreationUiState> = _uiState

    init {
        loadLastCreation()
        observeProgress()
    }

    private fun loadLastCreation() {
        viewModelScope.launch {
            repository.getLastCreation().collect { creation ->
                creation?.let {
                    _uiState.value = _uiState.value.copy(result = it)
                }
            }
        }
    }

    private fun observeProgress() {
        viewModelScope.launch {
            webSocketClient.progressUpdates.collect { (msg, prog) ->
                if (_uiState.value.isLoading) {
                    _uiState.value = _uiState.value.copy(statusMessage = msg, progress = prog)
                }
            }
        }
    }

    fun onEvent(event: CreationUiEvent) {
        when (event) {
            is CreationUiEvent.OnPromptChanged -> {
                _uiState.value = _uiState.value.copy(prompt = event.newPrompt)
            }
            is CreationUiEvent.OnGenerateClicked -> {
                generate()
            }
        }
    }

    private fun generate() {
        val currentPrompt = _uiState.value.prompt
        if (currentPrompt.isBlank()) return

        viewModelScope.launch {
            webSocketClient.connect("dummy_token")
            
            repository.generateAsset(CreationRequest(currentPrompt)).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true, 
                            error = null,
                            progress = 0f,
                            statusMessage = "Warming up AI engine..."
                        )
                    }
                    is Resource.Success -> {
                        webSocketClient.disconnect()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            result = resource.data,
                            progress = 1.0f,
                            statusMessage = "Creation complete!",
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        webSocketClient.disconnect()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}

data class CreationUiState(
    val prompt: String = "",
    val isLoading: Boolean = false,
    val progress: Float = 0f,
    val statusMessage: String? = null,
    val result: CreationResponse? = null,
    val error: String? = null
)

sealed class CreationUiEvent {
    data class OnPromptChanged(val newPrompt: String) : CreationUiEvent()
    object OnGenerateClicked : CreationUiEvent()
}
