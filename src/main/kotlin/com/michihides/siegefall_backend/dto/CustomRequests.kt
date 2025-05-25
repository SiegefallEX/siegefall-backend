package com.michihides.siegefall_backend.dto

import com.michihides.siegefall_backend.model.CustomCharacter
import com.michihides.siegefall_backend.model.CustomUser

class CustomRequests {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class UpdateCharactersRequest(
        val characters: MutableList<CustomCharacter>
    )

    data class UpdateDefenseRequest(
        val defense: MutableList<CustomCharacter?>
    )

    data class UpdateDiamondsRequest(
        val diamonds: Int
    )

    data class UpdateStaminaRequest(
        val stamina: Int
    )

    data class UpdateGoldRequest(
        val gold: Int
    )

    data class GetModePlayedAndOutcome(
        val caseGame: Int
    )

    data class GetRandomPlayer(
        val user: CustomUser
    )
}