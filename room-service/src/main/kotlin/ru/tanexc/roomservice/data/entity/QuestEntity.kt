package ru.tanexc.roomservice.data.entity

import jakarta.persistence.*
import ru.tanexc.roomservice.data.model.QuestPrivacy

@Entity
@Table(name="quests")
data class QuestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val privacy: QuestPrivacy,
    val owner: Long,
    val questions: List<Long>
) {
    fun isAccessGranted(userId: Long): Boolean {
        if (owner == userId) return true
        if (privacy == QuestPrivacy.PUBLIC) return true
        return false
    }
}
