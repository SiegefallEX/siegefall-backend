package com.michihides.siegefall_backend.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private var username: String = "",
    private var password: String = "",
    private var authorities: MutableCollection<GrantedAuthority> = mutableListOf()
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return authorities
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String? {
        return username
    }
}
