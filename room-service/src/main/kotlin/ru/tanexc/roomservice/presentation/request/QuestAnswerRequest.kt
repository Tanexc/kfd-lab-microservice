package ru.tanexc.roomservice.presentation.request

data class QuestAnswerRequest(
    val id: Long,
    val roomId: Long,
    val answers: List<AnswerRequest>
)