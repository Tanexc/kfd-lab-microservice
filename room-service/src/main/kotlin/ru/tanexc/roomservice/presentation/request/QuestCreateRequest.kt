package ru.tanexc.roomservice.presentation.request

import ru.tanexc.roomservice.data.model.QuestPrivacy

data class QuestCreateRequest(
    val title: String,
    val questions: List<Long>,
    val privacy: QuestPrivacy,
)