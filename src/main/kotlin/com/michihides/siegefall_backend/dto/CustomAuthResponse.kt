package com.michihides.siegefall_backend.dto

import com.michihides.siegefall_backend.model.CustomUser

class CustomAuthResponse {
    data class AuthResponse(
        val success: Boolean,
        val message: String,
        val token: String?,
        val username: String
    )

    data class GeneralUpdateResponse(
        val success: Boolean,
        val message: String
    )

    data class RandomPlayerResponse(
        val success: Boolean,
        val user: CustomUser,
        val message: String
    )

    data class AllPlayerResponse(
        val success: Boolean,
        val user: List<CustomUser>,
        val message: String
    )
}