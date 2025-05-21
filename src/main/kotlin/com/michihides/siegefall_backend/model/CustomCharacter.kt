package com.michihides.siegefall_backend.model

import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class CustomCharacter(
    var name: String,

    @Enumerated(EnumType.STRING)
    var team: Team,

    @Embedded
    var stats: Stats,

    @Embedded
    var profilePicture: ProfilePicture,

    @Embedded
    var animations: Animations,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
) {
    enum class Team {
        player,
        enemy
    }

    @Embeddable
    data class Stats(
        var attack: Int,
        var defense: Int,
        var maxHp: Int,
        var currentHp: Int,
        var speed: Int,
        @Enumerated(EnumType.STRING)
        var classType: ClassType,
        @Enumerated(EnumType.STRING)
        var attackType: AttackType,
        var level: Int
    ) {
        enum class ClassType { Warrior, Mage, Assassin }
        enum class AttackType { Front, Skip, Back }
    }

    @Embeddable
    data class ProfilePicture(
        var small: String,
        var big: String
    )

    @Embeddable
    data class Animations(
        @ElementCollection
        var idle: List<String> = listOf(),
        @ElementCollection
        var attack: List<String> = listOf(),
        @ElementCollection
        var faint: List<String> = listOf(),
        @ElementCollection
        var moveForward: List<String> = listOf(),
        @ElementCollection
        var moveBackward: List<String> = listOf(),
        @ElementCollection
        var getHit: List<String> = listOf()
    )
}
