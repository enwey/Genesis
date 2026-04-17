package com.genesis.app.ui.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.CreationRequest
import com.genesis.app.data.model.CreationResponse
import com.genesis.app.domain.repository.CreationRepository
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreationViewModel @Inject constructor(
    private val repository: CreationRepository
) : ViewModel() {

    private val _creationState = MutableStateFlow<Resource<CreationResponse>>(Resource.Loading())
    val creationState: StateFlow<Resource<CreationResponse>> = _creationState

    fun generate(prompt: String) {
        viewModelScope.launch {
            repository.generateAsset(CreationRequest(prompt)).collect {
                _creationState.value = it
            }
        }
    }
}
