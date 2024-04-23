package ru.tanexc.roomservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tanexc.roomservice.data.entity.QuestEntity

interface QuestRepository: JpaRepository<QuestEntity, Long> {
}