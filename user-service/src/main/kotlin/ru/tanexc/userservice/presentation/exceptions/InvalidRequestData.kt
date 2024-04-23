package ru.tanexc.userservice.presentation.exceptions

class InvalidRequestData(
    description: String? = null
) : BaseException(
    message = "invalid request data",
    statusCode = 406,
    description = description
)