package ru.tanexc.roomservice.presentation.request

data class RoomMemberRequest(
    val roomId: Long,
    val userId: Long
)