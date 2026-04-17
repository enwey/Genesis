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

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Loading())
    val loginState: StateFlow<Resource<LoginResponse>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(LoginRequest(email, password)).collect {
                _loginState.value = it
            }
        }
    }
}
