package ru.tanexc.roomservice.presentation.request

import ru.tanexc.roomservice.data.model.QuestPrivacy

data class QuestionCreateRequest(
    val privacy: QuestPrivacy,
    val question: String,
    val answer: String,
    val variants: List<String>,
)