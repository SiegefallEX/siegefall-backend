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
    val characters: Array<Int>,
    val defense: Array<Int>,
    val rankingNormalPvp: Int,
    val rankingColloseum: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
) {
}