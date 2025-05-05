package com.michihides.siegefall_backend.controller

import com.michihides.siegefall_backend.dto.CustomAuthResponse
import com.michihides.siegefall_backend.dto.CustomLoginRequest
import com.michihides.siegefall_backend.model.CustomUser
import com.michihides.siegefall_backend.repository.CustomUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    ): ResponseEntity<String> {
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

        return ResponseEntity.status(201).body("User was successfully created!")
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: CustomLoginRequest.LoginRequest): ResponseEntity<CustomAuthResponse.AuthResponse> {
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
}