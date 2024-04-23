package ru.tanexc.userservice.presentation.request

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.tanexc.userservice.data.model.Role
import ru.tanexc.userservice.data.model.User


data class SignUpRequest(
    val username: String,
    val name: String,
    val password: String,
    val role: Role
) {
    fun asDomain(): User = User(
        id = 0,
        name = name,
        username = username,
        role = role,
        password = passwordEncoder().encode(password)
    )

    private fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}