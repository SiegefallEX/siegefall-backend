package com.michihides.siegefall_backend.dto

import com.michihides.siegefall_backend.model.CustomUser

class CustomRequests {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class UpdateCharactersRequest(
        val characters: List<Int>
    )

    data class UpdateDefenseRequest(
        val defense: List<Int>
    )

    data class GetRandomPlayer(
        val user: CustomUser
    )
}