package com.michihides.siegefall_backend.repository

import com.michihides.siegefall_backend.model.CustomUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CustomUserRepository: JpaRepository<CustomUser, Long> {
    // Custom Query
    fun findByUsername(username: String): Optional<CustomUser>

    fun findByEmail(email: String): Optional<CustomUser>
}