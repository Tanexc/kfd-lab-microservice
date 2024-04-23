package ru.tanexc.roomservice.presentation.response

import ru.tanexc.roomservice.data.entity.Question
import ru.tanexc.roomservice.data.model.QuestPrivacy

class QuestResponse(
    val id: Long,
    val questions: List<Question>,
    val owner: Long,
    val privacy: QuestPrivacy
)