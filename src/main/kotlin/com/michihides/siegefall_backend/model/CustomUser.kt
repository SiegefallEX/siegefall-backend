package com.michihides.siegefall_backend.model

import com.michihides.siegefall_backend.config.DefenseAttributConverter
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany

@Entity
data class CustomUser(
    val email: String,
    val username: String,
    val password: String,
    val stamina: Int,
    val diamonds: Int,
    val gold: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_characters",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "custom_character_id")]
    )
    val characters: MutableList<CustomCharacter> = mutableListOf(),

    @Convert(converter = DefenseAttributConverter::class)
    @Column(columnDefinition = "TEXT")
    var defense: List<CustomCharacter?> = listOf(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),


    val rankingNormalPvp: Int,
    val rankingColloseum: Int,
    val pvmWins: Int,
    val pvmLosses: Int,
    val pvpWins: Int,
    val pvpLosses: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
)
