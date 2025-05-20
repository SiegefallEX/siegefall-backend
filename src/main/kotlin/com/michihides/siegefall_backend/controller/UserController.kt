package com.michihides.siegefall_backend.controller

import com.michihides.siegefall_backend.dto.CustomAuthResponse
import com.michihides.siegefall_backend.dto.CustomRequests
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
            newUser.rankingColloseum
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
        println("Receivued characters: ${request.characters}")

        val foundUserOptional = customUserRepository.findById(id)

        if (foundUserOptional.isEmpty) {
            return ResponseEntity.status(404).body(CustomAuthResponse.GeneralUpdateResponse(false, "User not found"))
        }

        val foundUser = foundUserOptional.get()

        val updatedUser = CustomUser(
            email = foundUser.email,
            username = foundUser.username,
            password = foundUser.password,
            stamina = foundUser.stamina,
            diamonds = foundUser.diamonds,
            gold = foundUser.gold,
            characters = request.characters,
            defense = foundUser.defense,
            rankingNormalPvp = foundUser.rankingNormalPvp,
            rankingColloseum = foundUser.rankingColloseum,
            id = foundUser.id
        )

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Characters successfully added!"))
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

        val updatedUser = CustomUser(
            email = foundUser.email,
            username = foundUser.username,
            password = foundUser.password,
            stamina = foundUser.stamina,
            diamonds = foundUser.diamonds,
            gold = foundUser.gold,
            characters = foundUser.characters,
            defense = request.defense,
            rankingNormalPvp = foundUser.rankingNormalPvp,
            rankingColloseum = foundUser.rankingColloseum,
            id = foundUser.id
        )

        customUserRepository.save(updatedUser)

        return ResponseEntity.ok(CustomAuthResponse.GeneralUpdateResponse(success = true, message = "Defense successfully updated!"))
    }


    @Scheduled(fixedRate = 360_000)
    fun staminaRefill() {
        val users = customUserRepository.findAll()

        users.forEach { user ->
            val updatedUser = user.copy(stamina = minOf(user.stamina + 1, 240))
            customUserRepository.save(updatedUser)
        }

        println("Stamina has been increased by 1 for all users!")
    }
}