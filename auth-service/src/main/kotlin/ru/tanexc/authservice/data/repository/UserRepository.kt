package ru.tanexc.authservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.tanexc.authservice.data.model.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User
}