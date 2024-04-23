package ru.tanexc.roomservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tanexc.roomservice.data.model.User

interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username: String): User
}