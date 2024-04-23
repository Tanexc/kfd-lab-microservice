package ru.tanexc.roomservice.data.entity

import jakarta.persistence.*
import ru.tanexc.roomservice.data.model.QuestPrivacy

@Entity
@Table(name="questions")
data class Question(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val privacy: QuestPrivacy,
    val question: String,
    val answer: String,
    val variants: List<String>,
    val owner: Long
) {
    fun isAccessGranted(userId: Long): Boolean {
        if (owner == userId) return true
        if (privacy == QuestPrivacy.PUBLIC) return true
        return false
    }
}