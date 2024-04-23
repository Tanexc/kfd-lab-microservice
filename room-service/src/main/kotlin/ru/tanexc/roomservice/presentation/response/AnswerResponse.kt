package ru.tanexc.roomservice.presentation.response

data class AnswerResponse(
    val id: Long,
    val answer: String,
    val correct: Boolean
)