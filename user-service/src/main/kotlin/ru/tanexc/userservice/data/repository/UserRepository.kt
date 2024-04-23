package ru.tanexc.userservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.tanexc.userservice.data.model.User

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User
}