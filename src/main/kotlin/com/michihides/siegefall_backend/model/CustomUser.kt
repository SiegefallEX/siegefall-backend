package com.michihides.siegefall_backend.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class CustomUser(
    val email: String,
    val username: String,
    val password: String,
    val stamina: Int,
    val diamonds: Int,
    val gold: Int,
    val characters: List<Int>,
    val defense: List<Int>,
    val rankingNormalPvp: Int,
    val rankingColloseum: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
) {
}