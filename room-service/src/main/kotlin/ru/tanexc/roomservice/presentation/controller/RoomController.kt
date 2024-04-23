package ru.tanexc.roomservice.presentation.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.tanexc.roomservice.data.entity.Room
import ru.tanexc.roomservice.data.model.QuestPrivacy
import ru.tanexc.roomservice.data.model.Role
import ru.tanexc.roomservice.data.repository.QuestRepository
import ru.tanexc.roomservice.data.repository.RoomRepository
import ru.tanexc.roomservice.presentation.request.RoomCreateRequest
import ru.tanexc.roomservice.presentation.request.RoomDeleteRequest
import ru.tanexc.roomservice.presentation.request.RoomMemberRequest
import ru.tanexc.roomservice.presentation.response.Response
import ru.tanexc.roomservice.presentation.response.RoomQuestRequest
import ru.tanexc.roomservice.util.JwtHelper
import ru.tanexc.userservice.presentation.exceptions.InvalidCredentials
import ru.tanexc.userservice.presentation.exceptions.NotFoundException
import ru.tanexc.userservice.presentation.exceptions.PermissionDenied
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/room")
class RoomController(
    private val roomRepository: RoomRepository,
    private val questRepository: QuestRepository,
    private val jwtHelper: JwtHelper
) {

    @PostMapping("/create")
    fun create(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomCreateRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        val room = roomRepository.save(
            Room(
                0,
                data.title,
                System.currentTimeMillis(),
                listOf(userId),
                data.quests
                    .filter { id ->
                        questRepository.findById(id).getOrNull()?.isAccessGranted(userId) ?: false
                    },
                userId
            )
        )
        return ResponseEntity.status(200).body(Response.DataResponse(room, "success"))
    }

    @PostMapping("/delete")
    fun delete(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomDeleteRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        val room = roomRepository.findById(data.id).getOrNull() ?: throw NotFoundException()
        if (room.owner == userId || role == Role.Admin) {
            roomRepository.delete(room)
        }
        return ResponseEntity.status(200).body(Response.DataResponse(null, "success"))
    }

    @PostMapping("/add-quest")
    fun addQuest(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomQuestRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        var room = roomRepository.findById(data.roomId).getOrNull() ?: throw NotFoundException("room not found")
        val quest = questRepository.findById(data.questId).getOrNull() ?: throw NotFoundException("quest not found")
        if (room.owner != userId && role != Role.Admin) throw PermissionDenied("")
        if (quest.isAccessGranted(userId) || role == Role.Admin) {
            room = roomRepository.save(room.copy(quests = room.quests + data.questId))
        }
        return ResponseEntity.status(200).body(Response.DataResponse(room, "success"))
    }

    @PostMapping("/delete-quest")
    fun deleteQuest(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomQuestRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        var room = roomRepository.findById(data.roomId).getOrNull() ?: throw NotFoundException("room not found")
        val quest = questRepository.findById(data.questId).getOrNull() ?: throw NotFoundException("quest not found")
        if (room.owner != userId && role != Role.Admin) throw PermissionDenied("")
        if (quest.isAccessGranted(userId) || role == Role.Admin) {
            room = roomRepository.save(room.copy(quests = room.quests - data.questId))
        }
        return ResponseEntity.status(200).body(Response.DataResponse(room, "success"))
    }

    @PostMapping("/add-member")
    fun addMember(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomMemberRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        var room = roomRepository.findById(data.roomId).getOrNull() ?: throw NotFoundException("room not found")

        if (room.owner != userId && role != Role.Admin) throw PermissionDenied("")
        room = roomRepository.save(room.copy(members = room.members + data.userId))
        return ResponseEntity.status(200).body(Response.DataResponse(room, "success"))
    }

    @PostMapping("/delete-member")
    fun deleteMember(
        @RequestHeader header: HttpHeaders,
        @RequestBody data: RoomMemberRequest
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        var room = roomRepository.findById(data.roomId).getOrNull() ?: throw NotFoundException("room not found")
        if (room.owner != userId && role != Role.Admin) throw PermissionDenied("")
        room = roomRepository.save(room.copy(quests = room.quests - data.userId))
        return ResponseEntity.status(200).body(Response.DataResponse(room, "success"))
    }

    @GetMapping("/quests")
    fun quests(
        @RequestHeader header: HttpHeaders,
        @RequestParam roomId: Long,
        @RequestParam questId: Long?
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: throw InvalidCredentials("token required")
        val userId = jwtHelper.extractUserId(token)
        val role = jwtHelper.extractUserRole(token)
        val room = roomRepository.findById(roomId).getOrNull() ?: throw NotFoundException("room not found")
        if (!room.members.contains(userId) && role != Role.Admin) throw PermissionDenied("")
        if (questId == null) {
            return ResponseEntity.status(200).body(Response.DataResponse(room.quests.mapNotNull { questRepository.findById(it).getOrNull() }, "success"))
        } else {
            if (!room.quests.contains(questId)) throw NotFoundException("room does not contain this quest")
            val quest = questRepository.findById(questId)
            return ResponseEntity.status(200).body(Response.DataResponse(quest, "success"))
        }

    }


}