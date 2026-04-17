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

    private val _balanceState = MutableStateFlow<Resource<WalletResponse>>(Resource.Loading())
    val balanceState: StateFlow<Resource<WalletResponse>> = _balanceState

    init {
        fetchBalance()
    }

    fun fetchBalance() {
        viewModelScope.launch {
            repository.getBalance().collect {
                _balanceState.value = it
            }
        }
    }
}
