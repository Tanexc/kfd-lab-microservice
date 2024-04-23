package ru.tanexc.roomservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tanexc.roomservice.data.entity.Room

interface RoomRepository: JpaRepository<Room, Long> {
}