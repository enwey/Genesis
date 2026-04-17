package com.genesis.app.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.WalletResponse
import com.genesis.app.domain.repository.WalletRepository
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    init {
        fetchBalance()
    }

    fun onEvent(event: WalletUiEvent) {
        when (event) {
            WalletUiEvent.OnRefresh -> fetchBalance()
            is WalletUiEvent.OnBuyManaClicked -> buyMana(event.amount)
        }
    }

    private fun fetchBalance() {
        viewModelScope.launch {
            repository.getBalance().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            wallet = resource.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                }
            }
        }
    }

    private fun buyMana(@Suppress("UNUSED_PARAMETER") amount: Int) {
        // Implement Google Play Billing flow here
    }
}

data class WalletUiState(
    val isLoading: Boolean = false,
    val wallet: WalletResponse? = null,
    val error: String? = null
)

sealed class WalletUiEvent {
    object OnRefresh : WalletUiEvent()
    data class OnBuyManaClicked(val amount: Int) : WalletUiEvent()
}
