package com.michihides.siegefall_backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class BCryptPasswordEncoderConfig {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {

        return BCryptPasswordEncoder(14) // 4-31 (weaker = faster)
    }
}