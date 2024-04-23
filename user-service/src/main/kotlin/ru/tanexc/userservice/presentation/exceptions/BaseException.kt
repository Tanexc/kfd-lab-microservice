package ru.tanexc.userservice.presentation.exceptions

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.ResponseEntity

sealed class BaseException(
    override val message: String,
    val description: String?,
    val statusCode: Int,
    @JsonIgnore
    override val cause: Throwable? = null,
    @JsonIgnore
    val stackTrace: String? = null,
    @JsonIgnore
    val suppressed: Throwable? = null
) : Exception() {
    fun asResponse(): ResponseEntity<Any> {
        return ResponseEntity.status(statusCode).body(this)
    }
}