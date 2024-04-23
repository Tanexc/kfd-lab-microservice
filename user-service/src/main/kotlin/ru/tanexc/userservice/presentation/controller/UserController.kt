package ru.tanexc.userservice.presentation.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.tanexc.userservice.data.model.User
import ru.tanexc.userservice.data.repository.UserRepository
import ru.tanexc.userservice.presentation.exceptions.BaseException
import ru.tanexc.userservice.presentation.exceptions.InvalidCredentials
import ru.tanexc.userservice.presentation.exceptions.NotFoundException
import ru.tanexc.userservice.presentation.exceptions.ServerError
import ru.tanexc.userservice.presentation.request.SignInRequest
import ru.tanexc.userservice.presentation.request.SignUpRequest
import ru.tanexc.userservice.presentation.response.Token
import ru.tanexc.userservice.util.JwtHelper
import kotlin.jvm.optionals.getOrNull

@RestController
class UserController(
    private val userRepository: UserRepository,
    private val jwtHelper: JwtHelper
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody signUpRequest: SignUpRequest,
    ): ResponseEntity<Any> {
        val user: User = userRepository.save(signUpRequest.asDomain())
        return ResponseEntity.status(200).body(user)
    }

    @GetMapping("/sign-in")
    fun signIn(
        @RequestBody signInRequest: SignInRequest
    ): ResponseEntity<Any> {
        logger.info("sign in")
        val user: User = userRepository.findUserByUsername(signInRequest.username)
        if (signInRequest.passwordEncoder().matches(signInRequest.password, user.password)) {
            return Token(
                jwtHelper.createToken(userDetails = user),
                jwtHelper.createToken(userDetails = user, isAccessToken = false)
            ).asResponse()
        } else {
            throw InvalidCredentials("invalid password")
        }
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestHeader header: HttpHeaders,
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: ""
        val userId = (jwtHelper.getClaims(token)?.get("id") as Int).toLong()
        val user = userRepository.findById(userId).getOrNull()?: throw NotFoundException()
        if (jwtHelper.isRefreshToken(token)) {
            val accessToken = jwtHelper.createToken(userDetails = user)
            val refreshToken = jwtHelper.createToken(userDetails = user, isAccessToken = false)
            return Token(accessToken, refreshToken).asResponse()
        } else {
            throw InvalidCredentials("refresh token required")
        }
    }

    @GetMapping("/getinfo")
    fun getInfo(
        @RequestHeader header: HttpHeaders,
        @RequestParam id: Long
    ): ResponseEntity<Any> {
        val token = header["Authorization"]?.first() ?: ""
        val userId = (jwtHelper.getClaims(token)?.get("id") as Int).toLong()
        val user = userRepository.findById(id).getOrNull()?: throw NotFoundException()
        return ResponseEntity.status(200).body(user.copy(password = ""))
    }

    @GetMapping("/keycloak-auth")
    fun kAuth() {
        val authentication = SecurityContextHolder.getContext().authentication;
    }
}