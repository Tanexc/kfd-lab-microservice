package ru.tanexc.roomservice.presentation.response

class QuestResultResponse(
    val correct: Long,
    val wrong: Long,
    val answers: List<AnswerResponse>
)