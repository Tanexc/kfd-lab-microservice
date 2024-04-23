package ru.tanexc.roomservice.presentation.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.tanexc.roomservice.data.entity.QuestEntity
import ru.tanexc.roomservice.data.model.QuestPrivacy
import ru.tanexc.roomservice.data.repository.QuestRepository
import ru.tanexc.roomservice.data.repository.QuestionRepository
import ru.tanexc.roomservice.data.repository.RoomRepository
import ru.tanexc.roomservice.presentation.request.QuestAnswerRequest
import ru.tanexc.roomservice.presentation.request.QuestCreateRequest
import ru.tanexc.roomservice.presentation.response.AnswerResponse
import ru.tanexc.roomservice.presentation.response.QuestResponse
import ru.tanexc.roomservice.presentation.response.Response
import ru.tanexc.roomservice.util.JwtHelper
import ru.tanexc.userservice.presentation.exceptions.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/quest")
class QuestController(
    private val questionRepository: QuestionRepository,
    private val questRepository: QuestRepository,
    private val roomRepository: RoomRepository,
    private val jwtHelper: JwtHelper
) {

    @ExceptionHandler(Exception::class, BaseException::class)
    fun exceptionHandler(e: Exception): ResponseEntity<Any> {
        return when (e) {
            is BaseException -> {
                ResponseEntity.status(e.statusCode).body(e)
            }

            else -> {
                ResponseEntity.status(500).body(ServerError(description = e.message))
            }
        }
    }


    @PostMapping("/create")
    fun create(
        @RequestHeader header: HttpHeaders,
        @RequestBody questCreateRequest: QuestCreateRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        questionRepository.findAllById(questCreateRequest.questions).forEach { question ->
            if (question.privacy == QuestPrivacy.PRIVATE && question.owner != userId) throw PermissionDenied("question with id = ${question.id} is private")
        }
        val quest = questRepository.save(
            QuestEntity(
                0,
                questCreateRequest.privacy,
                userId,
                questCreateRequest.questions
            )
        )

        return ResponseEntity.status(200).body(Response.DataResponse(quest, "success"))
    }

    @GetMapping("/answer")
    fun answer(
        @RequestHeader header: HttpHeaders,
        @RequestBody questAnswerRequest: QuestAnswerRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val quest = questRepository.findById(questAnswerRequest.id).getOrNull()?: throw NotFoundException()
        val room = roomRepository.findById(questAnswerRequest.roomId).getOrNull()?: throw NotFoundException()
        if (room.members.contains(userId) && room.quests.contains(quest.id)) {
            val answers = questionRepository.findAllById(quest.questions).map {question ->
                AnswerResponse(
                    question.id,
                    questAnswerRequest.answers.first{ it.id == question.id }.answer,
                    questAnswerRequest.answers.first{ it.id == question.id }.answer == question.answer
                )
            }

            return ResponseEntity.status(200).body(Response.DataResponse(answers, "success"))

        } else throw PermissionDenied()
    }

    @GetMapping("/get")
    fun get(
        @RequestHeader header: HttpHeaders,
        @RequestParam id: Long
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val quest = questRepository.findById(id).getOrNull()?: throw NotFoundException()
        if (quest.isAccessGranted(userId)) {
            val response = QuestResponse(
                quest.id,
                questionRepository.findAllById(quest.questions),
                quest.owner,
                quest.privacy
            )
            return ResponseEntity.status(200).body(Response.DataResponse(response, "success"))

        } else throw PermissionDenied()
    }
}