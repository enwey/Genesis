package com.genesis.app.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val username: String
)
