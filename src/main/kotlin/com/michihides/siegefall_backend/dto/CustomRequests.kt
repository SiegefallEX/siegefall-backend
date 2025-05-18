package com.michihides.siegefall_backend.dto

class CustomRequests {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class UpdateCharactersRequest(
        val characters: List<Int>
    )
}