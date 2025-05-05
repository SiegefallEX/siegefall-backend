package com.michihides.siegefall_backend.dto

class CustomAuthResponse {
    data class AuthResponse(
        val success: Boolean,
        val message: String,
        val token: String?,
        val username: String
    )
}