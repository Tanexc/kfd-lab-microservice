package ru.tanexc.roomservice.presentation.request

data class RoomCreateRequest(
    val title: String,
    val quests: List<Long>
)