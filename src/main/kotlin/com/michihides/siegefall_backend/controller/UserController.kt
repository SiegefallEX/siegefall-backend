package com.michihides.siegefall_backend.controller

import com.michihides.siegefall_backend.dto.CustomAuthResponse
import com.michihides.siegefall_backend.dto.CustomRequests
import com.michihides.siegefall_backend.model.CustomCharacter
import com.michihides.siegefall_backend.model.CustomUser
import com.michihides.siegefall_backend.repository.CustomUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    @Autowired val customUserRepository: CustomUserRepository,
    @Autowired val passwordEncoder: PasswordEncoder
) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<CustomUser>> {

        val result: List<CustomUser> = customUserRepository.findAll()

        return ResponseEntity.ok(result)
    }

    @GetMapping("/{username}")
    fun getUserByUserId(
        @PathVariable username: String
    ): ResponseEntity<CustomUser> {
        val foundUserOptional = customUserRepository.findByUsername(username)

        if (foundUserOptional.isPresent) {
            return ResponseEntity.ok(foundUserOptional.get())
        }

        return ResponseEntity.notFound().build()
    }

    @PostMapping("/random-enemy")
    fun getRandomUser(@RequestBody request: CustomRequests.GetRandomPlayer): ResponseEntity<CustomAuthResponse.RandomPlayerResponse> {
        val result: List<CustomUser> = customUserRepository.findAll()
        val resultNotPlayerOrEmptyDefense = result.filter { it.id != request.user.id && it.defense.isNotEmpty() }

        val randomUser = resultNotPlayerOrEmptyDefense.randomOrNull()

        return ResponseEntity.ok(CustomAuthResponse.RandomPlayerResponse(success = randomUser != null, user = randomUser ?: request.user, message = if (randomUser != null) "Random Enemy Successfully found!" else "No valid enemy available"))
    }


    @PostMapping("/register")
    fun createUser(
        @Validated @RequestBody newUser: CustomUser
    ): ResponseEntity<CustomAuthResponse.AuthResponse> {
        val bcryptUser = CustomUser(
            newUser.email,
            newUser.username,
            passwordEncoder.encode(newUser.password),
            newUser.stamina,
            newUser.diamonds,
            newUser.gold,
            newUser.characters,
            newUser.defense,
            newUser.rankingNormalPvp,
            newUser.rankingColloseum,
            newUser.pvmWins,
            newUser.pvmLosses,
            newUser.pvpWins,
            newUser.pvpLosses
        )

        customUserRepository.save(bcryptUser)

        return ResponseEntity.ok(CustomAuthResponse.AuthResponse(true, "Register successful!", "Placeholder Token, TODO - IMPLEMENT", newUser.username))
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: CustomRequests.LoginRequest): ResponseEntity<CustomAuthResponse.AuthResponse> {
        val foundUserOptional = customUserRepository.findByEmail(loginRequest.email)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.AuthResponse(false, "User not found", "", ""))
        }

        val foundUser = foundUserOptional.get()

        if (!passwordEncoder.matches(loginRequest.password, foundUser.password)) {
            return ResponseEntity.status(401).body(CustomAuthResponse.AuthResponse(false, "Invalid email or password", "", ""))
        }

        return ResponseEntity.ok(CustomAuthResponse.AuthResponse(true, "Login successful!", "Placeholder Token, TODO - IMPLEMENT", foundUser.username))
    }

    @PutMapping("/update-characters")
    fun updateCharacters(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateCharactersRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404)
                .body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()
        val foundUserCurrentCharactersToUpdate = foundUser.characters.toMutableList()

        // Checks if incoming characters matches a character name that has already been obtained
        request.characters.forEach { character ->
            val index = foundUserCurrentCharactersToUpdate.indexOfFirst { it.name == character.name }
            if (index >= 0) {
                val dupeCharacterFound = foundUserCurrentCharactersToUpdate[index]
                val leveledUpCharacter = customCharacterLevelUp(dupeCharacterFound)
                foundUserCurrentCharactersToUpdate[index] = leveledUpCharacter
            } else {
                foundUserCurrentCharactersToUpdate.add(character)
            }
        }

        // To check if defense has the character as well to update it accordingly
        val foundUserCurrentDefenseToUpdate = foundUser.defense.toMutableList()
        for (i in foundUserCurrentDefenseToUpdate.indices) {
            val character = foundUserCurrentDefenseToUpdate[i]
            if (character != null) {
                val characterUpdated = foundUserCurrentCharactersToUpdate.firstOrNull { it.name == character.name }
                if (characterUpdated != null) {
                    foundUserCurrentDefenseToUpdate[i] = characterUpdated
                }
            }
        }

        val updatedUser = foundUser.copy(characters = foundUserCurrentCharactersToUpdate, defense = foundUserCurrentDefenseToUpdate)
        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Characters successfully updated!"))
    }

    // Level up function that gets called if a character is rolled and already exists in the characters array
    fun customCharacterLevelUp(character: CustomCharacter): CustomCharacter {
        val preLevelStats = character.stats
        val afterLevelStats = CustomCharacter.Stats(
            attack = preLevelStats.attack + (preLevelStats.attack / 5),
            defense = preLevelStats.defense + (preLevelStats.defense / 5),
            maxHp = preLevelStats.maxHp + (preLevelStats.maxHp / 5),
            currentHp = preLevelStats.maxHp + (preLevelStats.maxHp / 5),
            speed = preLevelStats.speed + (preLevelStats.speed / 5),
            classType = preLevelStats.classType,
            attackType = preLevelStats.attackType,
            level = preLevelStats.level + 1
        )

        return character.copy(stats = afterLevelStats)
    }


    @PutMapping("/update-defense")
    fun updateDefense(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateDefenseRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        println("Defense Updated with: ${request.defense}")

        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val mergingDefenseArrays: List<CustomCharacter?> = request.defense.map { character ->
            if (character == null) {
                null
            } else {
                val persisted = foundUser.characters.find { it.name == character.name }
                if (persisted != null) {
                    character.copy(id = persisted.id)
                } else {
                    character
                }
            }
        }

        val updatedUser = foundUser.copy(defense = mergingDefenseArrays)
        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Defense successfully updated!"))
    }

    @PutMapping("/update-end-of-game")
    fun updateEndOfGame(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.GetModePlayedAndOutcome
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = when (request.caseGame) {
            1 -> foundUser.copy(pvmWins = foundUser.pvmWins + 1)
            2 -> foundUser.copy(pvmLosses = foundUser.pvmLosses + 1)
            3 -> foundUser.copy(pvpWins = foundUser.pvpWins + 1)
            4 -> foundUser.copy(pvpLosses = foundUser.pvpLosses + 1)
            else -> foundUser
        }

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "End of game stats updated without problem!"))
    }

    @PutMapping("/update-diamonds")
    fun updateDiamonds(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateDiamondsRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = foundUser.copy(diamonds = foundUser.diamonds + request.diamonds)

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Diamonds have been updated!"))
    }

    @PutMapping("/update-diamonds-subtract")
    fun updateDiamondsSubtract(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateDiamondsRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = foundUser.copy(diamonds = foundUser.diamonds - request.diamonds)

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Diamonds have been updated!"))
    }

    @PutMapping("/update-stamina")
    fun updateStamina(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateStaminaRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = foundUser.copy(stamina = foundUser.stamina + request.stamina)

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Stamina have been updated!"))
    }

    @PutMapping("/update-gold")
    fun updateGold(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateGoldRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = foundUser.copy(gold = foundUser.gold + request.gold)

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Gold have been updated!"))
    }

    @PutMapping("/update-gold-subtract")
    fun updateGoldSubtract(
        @RequestParam("id") id: Long,
        @RequestBody request: CustomRequests.UpdateGoldRequest
    ): ResponseEntity<CustomAuthResponse.GeneralUpdateResponse> {
        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser: CustomUser = foundUser.copy(gold = foundUser.gold - request.gold)

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Gold have been updated!"))
    }

    @Scheduled(fixedRate = 360_000)
    fun staminaRefill() {
        val users = customUserRepository.findAll()

        users.forEach { user ->
            if (user.stamina <= 240) {
                val updatedUser = user.copy(stamina = minOf(user.stamina + 1, 240))
                customUserRepository.save(updatedUser)
            }
        }

        println("Stamina has been increased by 1 for all users!")
    }
}