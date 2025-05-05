package com.michihides.siegefall_backend.dto

class CustomLoginRequest {
    data class LoginRequest(
        val email: String,
        val password: String
    )
}