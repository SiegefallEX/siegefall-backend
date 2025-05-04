package com.michihides.siegefall_backend.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class AppSecurityConfig(
    @Autowired val passwordEncoder: PasswordEncoder
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Remember to remove before deploy
            .authorizeHttpRequests { it // Same as auth -> auth (lambda)
                .requestMatchers("/", "/login", "/logout", "/user", "/user/password").permitAll()
                .requestMatchers("user/authenticated/admin").hasRole("ADMIN")
                .requestMatchers("/user/authenticated/manager").hasRole("MANAGER")
                .anyRequest().permitAll()
            }
            .formLogin {} // Is default

        return http.build()
    }
}