package ru.tanexc.authservice.data

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import ru.tanexc.authservice.data.repository.UserRepository

@Service
class AuthUserDetailsService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails = userRepository.findUserByUsername(username)
}