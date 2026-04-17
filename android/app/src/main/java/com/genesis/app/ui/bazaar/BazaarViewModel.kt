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
import javax.inject.Inject

@HiltViewModel
class BazaarViewModel @Inject constructor(
    private val repository: BazaarRepository
) : ViewModel() {

    private val _assetsState = MutableStateFlow<Resource<List<CreationResponse>>>(Resource.Loading())
    val assetsState: StateFlow<Resource<List<CreationResponse>>> = _assetsState

    init {
        fetchAssets()
    }

    fun fetchAssets() {
        viewModelScope.launch {
            repository.getAssets().collect {
                _assetsState.value = it
            }
        }
    }
}
