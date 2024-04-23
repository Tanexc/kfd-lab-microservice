package ru.tanexc.roomservice.data.entity

import jakarta.persistence.*

@Entity
@Table(name="rooms")
data class Room(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    val timestamp: Long,
    val members: List<Long>,
    val quests: List<Long>,
    val owner: Long,
)