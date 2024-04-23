package ru.tanexc.userservice.presentation.exceptions

class NotFoundException(
    description: String? = null
) : BaseException(
    message = "not found",
    statusCode = 404,
    description = description
)