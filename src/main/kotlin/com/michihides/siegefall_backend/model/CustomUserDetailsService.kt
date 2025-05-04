package com.michihides.siegefall_backend.model

import com.michihides.siegefall_backend.repository.CustomUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class CustomUserDetailsService(
    @Autowired val customUserRepository: CustomUserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        // Find User by Username - QUERY
        if (username == null) { throw NullPointerException("Username is nil!") }

        val optionalUser: Optional<CustomUser> = customUserRepository.findByUsername(username)

        if (optionalUser.isPresent) {
            print("Username was found!")

            val user = optionalUser.get()

            return CustomUserDetails(
                user.username,
                user.password,
            )
        } else {
            throw UsernameNotFoundException("Username wasn't found!")
        }
    }
}