package ru.tanexc.userservice.presentation.request

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

data class SignInRequest(
    val username: String,
    val password: String,
) {

    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}