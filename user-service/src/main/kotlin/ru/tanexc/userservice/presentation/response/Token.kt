package ru.tanexc.userservice.presentation.response

import org.springframework.http.ResponseEntity

data class Token(
    val access_token: String,
    val refresh_token: String,
) {
    fun asResponse(): ResponseEntity<Any> = ResponseEntity.status(200).body(this)
}
