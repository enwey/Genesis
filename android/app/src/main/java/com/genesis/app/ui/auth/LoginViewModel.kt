package com.genesis.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genesis.app.data.model.LoginRequest
import com.genesis.app.data.model.LoginResponse
import com.genesis.app.domain.repository.AuthRepository
import com.genesis.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnEmailChanged -> _uiState.value = _uiState.value.copy(email = event.email)
            is LoginUiEvent.OnPasswordChanged -> _uiState.value = _uiState.value.copy(password = event.password)
            LoginUiEvent.OnLoginClicked -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            repository.login(LoginRequest(_uiState.value.email, _uiState.value.password)).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = resource.message)
                }
            }
        }
    }
}

data class LoginUiState(
    val email: String = "test@genesis.com", // 默認測試郵箱
    val password: String = "password123", // 默認測試密碼
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed class LoginUiEvent {
    data class OnEmailChanged(val email: String) : LoginUiEvent()
    data class OnPasswordChanged(val password: String) : LoginUiEvent()
    object OnLoginClicked : LoginUiEvent()
}
