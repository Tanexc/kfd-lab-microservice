package ru.tanexc.roomservice.presentation.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.tanexc.roomservice.data.entity.Question
import ru.tanexc.roomservice.data.model.Role
import ru.tanexc.roomservice.data.repository.QuestionRepository
import ru.tanexc.roomservice.presentation.request.QuestionCreateRequest
import ru.tanexc.roomservice.presentation.request.RoomDeleteRequest
import ru.tanexc.roomservice.presentation.response.Response
import ru.tanexc.roomservice.util.JwtHelper
import ru.tanexc.userservice.presentation.exceptions.InvalidCredentials
import ru.tanexc.userservice.presentation.exceptions.NotFoundException
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/question")
class QuestionController(
    private val jwtHelper: JwtHelper,
    private val questionRepository: QuestionRepository
) {
    @PostMapping("/create")
    fun create(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: QuestionCreateRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val question = questionRepository.save(
            Question(
                0,
                data.privacy,
                data.question,
                data.answer,
                data.variants,
                userId
            )
        )
        return ResponseEntity.status(200).body(Response.DataResponse(question, "success"))
    }

    @PostMapping("/delete")
    fun delete(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomDeleteRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        val question = questionRepository.findById(data.id).getOrNull()?: throw NotFoundException("question not found")
        if (question.isAccessGranted(userId) || role == Role.Admin) questionRepository.delete(question)
        return ResponseEntity.status(200).body(Response.DataResponse(null, "success"))
    }
}