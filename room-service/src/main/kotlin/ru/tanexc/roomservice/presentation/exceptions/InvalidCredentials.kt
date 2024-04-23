package ru.tanexc.userservice.presentation.exceptions

class InvalidCredentials(
    description: String? = null
) : BaseException(
    message = "invalid credentials",
    statusCode = 406,
    description = description
)